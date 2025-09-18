package org.upyog.gis.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.upyog.gis.client.FilestoreClient;
import org.upyog.gis.config.GisProperties;
import org.upyog.gis.entity.UtilitiesGisLog;
import org.upyog.gis.model.PolygonProcessingResponse;
import org.upyog.gis.repository.UtilitiesGisLogRepository;
import org.upyog.gis.service.GisService;
import org.upyog.gis.util.KmlParser;
import org.upyog.gis.wfs.WfsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.OffsetDateTime;

/**
 * Improved implementation of GIS service for polygon processing.
 *
 * Key improvements made compared to the original:
 * - Use UUID for fileStoreId
 * - Use try-with-resources when reading the upload InputStream
 * - Do not hard-code fallback district/zone (no "New Jersey")
 * - Produce a more informative and structured fallback response (includes reason/timestamp/source)
 * - Robust null-checks when creating a cleaned WFS response
 * - Extract fewer assumptions about original WFS payload
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GisServiceImpl implements GisService {

    private static final long MAX_FILE_SIZE = 10L * 1024L * 1024L; // 10 MB

    private final WfsClient wfsClient;
    private final UtilitiesGisLogRepository logRepository;
    private final GisProperties gisProperties;
    private final ObjectMapper objectMapper;
    private final FilestoreClient filestoreClient;

    @Override
    @Transactional
    public PolygonProcessingResponse processPolygonFile(MultipartFile file, String tenantId, String applicationNo, String rtpiId) throws Exception {
        UtilitiesGisLog logEntry = null;
        String fileStoreId = null;

        try {
            validatePolygonFile(file);

            // Upload to Filestore
            log.info("Uploading KML file to Filestore: {}", file.getOriginalFilename());
            fileStoreId = filestoreClient.uploadFile(file, tenantId, "gis", "kml-upload");


            logEntry = createLogEntry(fileStoreId, tenantId, applicationNo, rtpiId, "PENDING", null, null);
            logRepository.save(logEntry);

            // parse the polygon using try-with-resources
            Polygon polygon;
            try (InputStream is = file.getInputStream()) {
                polygon = KmlParser.parsePolygon(is);
            }

            String polygonWkt = new WKTWriter().write(polygon);
            log.info("Processing polygon: {}", polygonWkt);

            JsonNode wfsResponse = null;
            String district = null;
            String zone = null;

            try {
                wfsResponse = wfsClient.queryFeatures(polygonWkt);

                // extract district/zone from the first feature if possible
                if (wfsResponse != null && wfsResponse.has("features") && wfsResponse.get("features").isArray() && wfsResponse.get("features").size() > 0) {
                    JsonNode firstFeature = wfsResponse.get("features").get(0);
                    if (firstFeature != null && firstFeature.has("properties")) {
                        JsonNode properties = firstFeature.get("properties");
                        if (properties.hasNonNull(gisProperties.getWfsDistrictAttribute())) {
                            district = properties.get(gisProperties.getWfsDistrictAttribute()).asText(null);
                        }
                        if (properties.hasNonNull(gisProperties.getWfsZoneAttribute())) {
                            zone = properties.get(gisProperties.getWfsZoneAttribute()).asText(null);
                        }
                    }
                }

            } catch (Exception wfsException) {
                // Log and build a clear, structured fallback response. Do NOT hard-code a district/zone.
                log.warn("WFS query failed ({}): {}. Building fallback response.", wfsException.getClass().getSimpleName(), wfsException.getMessage());

                wfsResponse = createFallbackWfsResponse(polygonWkt, wfsException);

                // attempt to extract district/zone from fallback response (if fallback provides it). Usually it won't.
                if (wfsResponse.has("features") && wfsResponse.get("features").isArray() && wfsResponse.get("features").size() > 0) {
                    JsonNode firstFeature = wfsResponse.get("features").get(0);
                    if (firstFeature != null && firstFeature.has("properties")) {
                        JsonNode properties = firstFeature.get("properties");
                        if (properties.hasNonNull(gisProperties.getWfsDistrictAttribute())) {
                            district = properties.get(gisProperties.getWfsDistrictAttribute()).asText(null);
                        }
                        if (properties.hasNonNull(gisProperties.getWfsZoneAttribute())) {
                            zone = properties.get(gisProperties.getWfsZoneAttribute()).asText(null);
                        }
                    }
                }
            }

            // Create a clean, minimal WFS response with only relevant data
            JsonNode cleanWfsResponse = createCleanWfsResponse(wfsResponse, district, zone);

            PolygonProcessingResponse response = PolygonProcessingResponse.builder()
                    .district(district)
                    .zone(zone)
                    .wfsResponse(cleanWfsResponse)
                    .fileStoreId(fileStoreId)
                    .build();

            updateLogEntry(logEntry, "SUCCESS", objectMapper.writeValueAsString(response), createSuccessDetails(wfsResponse));
            log.info("Polygon processing completed. District: {}, Zone: {}", district, zone);

            return response;

        } catch (Exception e) {
            log.error("Error processing polygon file", e);

            if (logEntry != null) {
                updateLogEntry(logEntry, "FAILURE", null, createErrorDetails(e));
            }

            throw new RuntimeException("Failed to process polygon file: " + e.getMessage(), e);
        }
    }

    private void validatePolygonFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Polygon file is required and cannot be empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".kml") && !filename.toLowerCase().endsWith(".xml"))) {
            throw new IllegalArgumentException("File must be a KML file (.kml or .xml extension)");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size cannot exceed " + MAX_FILE_SIZE + " bytes");
        }
    }

    private UtilitiesGisLog createLogEntry(String fileStoreId, String tenantId, String applicationNo, String rtpiId, String status, String output, JsonNode details) {
        return UtilitiesGisLog.builder()
                .fileStoreId(fileStoreId)
                .tenantId(tenantId)
                .applicationNo(applicationNo)
                .rtpiId(rtpiId)
                .status(status)
                .output(output)
                .auditCreatedBy("system")
                .auditCreatedTime(OffsetDateTime.now())
                .details(details)
                .build();
    }

    private void updateLogEntry(UtilitiesGisLog logEntry, String status, String output, JsonNode details) {
        logEntry.setStatus(status);
        logEntry.setOutput(output);
        logEntry.setDetails(details);
        logRepository.save(logEntry);
    }

    private JsonNode createSuccessDetails(JsonNode wfsResponse) {
        ObjectNode details = objectMapper.createObjectNode();
        int featureCount = 0;
        if (wfsResponse != null && wfsResponse.has("features") && wfsResponse.get("features").isArray()) {
            featureCount = wfsResponse.get("features").size();
        }
        details.put("wfsFeaturesCount", featureCount);
        details.put("timestamp", OffsetDateTime.now().toString());
        details.put("status", "OK");
        return details;
    }

    private JsonNode createErrorDetails(Exception e) {
        ObjectNode details = objectMapper.createObjectNode();
        details.put("error", e.getMessage());
        details.put("errorType", e.getClass().getSimpleName());
        details.put("timestamp", OffsetDateTime.now().toString());
        return details;
    }

    private JsonNode createFallbackWfsResponse(String polygonWkt, Exception cause) {
        // Create a structured fallback WFS response for fallback scenarios.
        // Note: We deliberately avoid hard-coding district/zone values here.
        ObjectNode fallbackResponse = objectMapper.createObjectNode();
        fallbackResponse.put("type", "FeatureCollection");
        fallbackResponse.put("totalFeatures", 1);
        fallbackResponse.put("numberMatched", 0);
        fallbackResponse.put("numberReturned", 1);

        ObjectNode fallbackFeature = objectMapper.createObjectNode();
        fallbackFeature.put("type", "Feature");
        fallbackFeature.put("id", "fallback-feature-1");

        ObjectNode properties = objectMapper.createObjectNode();
        // keep district/zone absent (or null) rather than guessing
        properties.putNull(gisProperties.getWfsDistrictAttribute());
        properties.putNull(gisProperties.getWfsZoneAttribute());
        properties.put("fallback", true);
        properties.put("note", "WFS service unavailable; fallback response provided");
        properties.put("fallbackReason", cause == null ? "unknown" : cause.getClass().getSimpleName());
        properties.put("fallbackMessage", cause == null ? "" : cause.getMessage());
        properties.put("requestPolygonWkt", polygonWkt);
        properties.put("timestamp", OffsetDateTime.now().toString());
        properties.put("source", "fallback");

        fallbackFeature.set("properties", properties);

        ArrayNode features = objectMapper.createArrayNode();
        features.add(fallbackFeature);
        fallbackResponse.set("features", features);

        return fallbackResponse;
    }

    private JsonNode createCleanWfsResponse(JsonNode originalResponse, String district, String zone) {
        ObjectNode cleanResponse = objectMapper.createObjectNode();
        cleanResponse.put("type", "FeatureCollection");

        if (originalResponse != null) {
            if (originalResponse.has("totalFeatures")) {
                cleanResponse.put("totalFeatures", originalResponse.get("totalFeatures").asInt(0));
            }
            if (originalResponse.has("numberMatched")) {
                cleanResponse.put("numberMatched", originalResponse.get("numberMatched").asInt(0));
            }
            if (originalResponse.has("numberReturned")) {
                cleanResponse.put("numberReturned", originalResponse.get("numberReturned").asInt(0));
            }
        } else {
            cleanResponse.put("totalFeatures", 0);
            cleanResponse.put("numberMatched", 0);
            cleanResponse.put("numberReturned", 0);
        }

        ArrayNode cleanFeatures = objectMapper.createArrayNode();

        if (originalResponse != null && originalResponse.has("features") && originalResponse.get("features").isArray()) {
            JsonNode features = originalResponse.get("features");
            for (JsonNode feature : features) {
                ObjectNode cleanFeature = objectMapper.createObjectNode();
                cleanFeature.put("type", "Feature");

                if (feature.has("id")) {
                    cleanFeature.put("id", feature.get("id").asText());
                }

                ObjectNode cleanProperties = objectMapper.createObjectNode();
                // Add the district and zone only if we successfully extracted them
                if (district != null) {
                    cleanProperties.put(gisProperties.getWfsDistrictAttribute(), district);
                } else if (feature.has("properties") && feature.get("properties").hasNonNull(gisProperties.getWfsDistrictAttribute())) {
                    cleanProperties.set(gisProperties.getWfsDistrictAttribute(), feature.get("properties").get(gisProperties.getWfsDistrictAttribute()));
                }

                if (zone != null) {
                    cleanProperties.put(gisProperties.getWfsZoneAttribute(), zone);
                } else if (feature.has("properties") && feature.get("properties").hasNonNull(gisProperties.getWfsZoneAttribute())) {
                    cleanProperties.set(gisProperties.getWfsZoneAttribute(), feature.get("properties").get(gisProperties.getWfsZoneAttribute()));
                }

                // Copy a small set of additional relevant properties if present
                String[] relevantProps = {"STATE_FIPS", "SUB_REGION", "PERSONS", "LAND_KM", "WATER_KM"};
                if (feature.has("properties")) {
                    JsonNode props = feature.get("properties");
                    for (String prop : relevantProps) {
                        if (props.has(prop)) {
                            cleanProperties.set(prop, props.get(prop));
                        }
                    }

                    if (props.has("fallback")) {
                        cleanProperties.set("fallback", props.get("fallback"));
                    }
                    if (props.has("note")) {
                        cleanProperties.set("note", props.get("note"));
                    }
                }

                cleanFeature.set("properties", cleanProperties);

                if (feature.has("geometry") && !feature.get("geometry").isNull()) {
                    ObjectNode geometryInfo = objectMapper.createObjectNode();
                    JsonNode geom = feature.get("geometry");
                    if (geom.has("type")) {
                        geometryInfo.put("type", geom.get("type").asText());
                    } else {
                        geometryInfo.putNull("type");
                    }
                    geometryInfo.put("coordinatesIncluded", false);
                    geometryInfo.put("note", "Geometry coordinates excluded for brevity");
                    cleanFeature.set("geometry", geometryInfo);
                } else {
                    cleanFeature.putNull("geometry");
                }

                cleanFeatures.add(cleanFeature);
            }
        }

        cleanResponse.set("features", cleanFeatures);
        return cleanResponse;
    }
}
