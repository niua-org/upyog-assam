package org.upyog.gis.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.upyog.gis.client.FilestoreClient;
import org.upyog.gis.config.GisProperties;
import org.upyog.gis.entity.GisLog;
import org.upyog.gis.model.GISResponse;
import org.upyog.gis.model.GISRequestWrapper;
import org.upyog.gis.model.GISRequest;
import org.egov.common.contract.response.ResponseInfo;
import org.upyog.gis.repository.GisLogRepository;
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
import java.time.Instant;

/**
 * Service implementation for GIS operations such as finding zone information from polygon files,
 * interacting with WFS, and logging GIS-related activities.
 * Handles KML uploads, WFS queries, and response formatting.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GisServiceImpl implements GisService {

    private static final long MAX_FILE_SIZE = 10L * 1024L * 1024L; // 10 MB

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILURE = "FAILURE";
    private static final String STATUS_PENDING = "PENDING";

    private final WfsClient wfsClient;
    private final GisLogRepository logRepository;
    private final GisProperties gisProperties;
    private final ObjectMapper objectMapper;
    private final FilestoreClient filestoreClient;

    /**
     * Finds zone information from a polygon file (KML/XML), uploads it to filestore, parses the polygon,
     * queries WFS for district/zone, logs the operation, and returns a structured response.
     *
     * @param file the uploaded polygon file (KML/XML)
     * @param gisRequestWrapper the GIS request wrapper containing RequestInfo and GIS request data
     * @return structured response containing district, zone, and WFS data
     * @throws Exception if any processing step fails
     */
    @Override
    @Transactional
    public GISResponse findZoneFromPolygon(MultipartFile file, GISRequestWrapper gisRequestWrapper) throws Exception {
        GISRequest gisRequest = gisRequestWrapper.getGisRequest();
        GisLog logEntry = null;
        String fileStoreId = null;

        try {
            validatePolygonFile(file);

            // Upload to Filestore
            log.info("Uploading KML file to Filestore: {}", file.getOriginalFilename());
            fileStoreId = filestoreClient.uploadFile(file, gisRequest.getTenantId(), "gis", "kml-upload");
            log.info("File uploaded successfully with ID: {}", fileStoreId);

            // Create initial PENDING log entry
            logEntry = createGisLog(gisRequest.getApplicationNo(), gisRequest.getRtpiId(), fileStoreId, 
                    gisRequest.getTenantId(), STATUS_PENDING, "PENDING", "Processing started", null, 
                    gisRequestWrapper.getRequestInfo() != null && gisRequestWrapper.getRequestInfo().getUserInfo() != null 
                        ? gisRequestWrapper.getRequestInfo().getUserInfo().getUuid() : "system");
            logRepository.save(logEntry);

            // Parse KML to get polygon
            log.info("Parsing KML file to extract polygon geometry");
            Polygon polygon = parseKmlFile(file);
            log.info("Successfully parsed polygon with {} vertices", polygon.getCoordinates().length);

            // Convert polygon to WKT format for WFS query
            WKTWriter wktWriter = new WKTWriter();
            String polygonWkt = wktWriter.write(polygon);
            log.info("Converted polygon to WKT: {}", polygonWkt);

            // Query WFS for district/zone information
            log.info("Querying WFS for district/zone information");
            JsonNode wfsResponse = wfsClient.queryFeatures(polygonWkt);
            log.info("WFS query completed successfully");

            // Extract district and zone from WFS response
            String district = extractDistrict(wfsResponse);
            String zone = extractZone(wfsResponse);
            log.info("Extracted district: {}, zone: {}", district, zone);

            // Create details for logging
            ObjectNode detailsJson = objectMapper.createObjectNode();
            detailsJson.put("fileName", file.getOriginalFilename());
            detailsJson.put("fileSize", file.getSize());
            detailsJson.put("district", district);
            detailsJson.put("zone", zone);
            detailsJson.put("polygonVertices", polygon.getCoordinates().length);

            // Update log entry with success status
            logEntry.setStatus(STATUS_SUCCESS);
            logEntry.setResponseStatus("SUCCESS");
            logEntry.setResponseJson("Successfully processed polygon and found district/zone");
            logEntry.setDetails(detailsJson);
            logEntry.setLastmodifiedby(gisRequestWrapper.getRequestInfo() != null && gisRequestWrapper.getRequestInfo().getUserInfo() != null 
                    ? gisRequestWrapper.getRequestInfo().getUserInfo().getUuid() : "system");
            logEntry.setLastmodifiedtime(Instant.now().toEpochMilli());
            logRepository.save(logEntry);

            // Clean WFS response for return
            JsonNode cleanWfsResponse = cleanWfsResponse(wfsResponse);

            // Create ResponseInfo
            ResponseInfo responseInfo = ResponseInfo.builder()
                    .apiId(gisRequestWrapper.getRequestInfo() != null ? gisRequestWrapper.getRequestInfo().getApiId() : null)
                    .ver(gisRequestWrapper.getRequestInfo() != null ? gisRequestWrapper.getRequestInfo().getVer() : null)
                    .ts(Instant.now().toEpochMilli())
                    .resMsgId("uief87324")
                    .msgId(gisRequestWrapper.getRequestInfo() != null ? gisRequestWrapper.getRequestInfo().getMsgId() : null)
                    .status("SUCCESSFUL")
                    .build();

            // Return successful response
            return GISResponse.builder()
                    .responseInfo(responseInfo)
                    .district(district)
                    .zone(zone)
                    .wfsResponse(cleanWfsResponse)
                    .fileStoreId(fileStoreId)
                    .build();

        } catch (Exception e) {
            log.error("Error finding zone from polygon file: {}", e.getMessage(), e);

            // Update log entry with failure status
            if (logEntry != null) {
                ObjectNode errorDetails = objectMapper.createObjectNode();
                errorDetails.put("fileName", file.getOriginalFilename());
                errorDetails.put("error", e.getMessage());
                if (fileStoreId != null) {
                    errorDetails.put("fileStoreId", fileStoreId);
                }
                
                logEntry.setStatus(STATUS_FAILURE);
                logEntry.setResponseStatus("FAILURE");
                logEntry.setResponseJson(e.getMessage());
                logEntry.setDetails(errorDetails);
                logEntry.setLastmodifiedby(gisRequestWrapper.getRequestInfo() != null && gisRequestWrapper.getRequestInfo().getUserInfo() != null 
                        ? gisRequestWrapper.getRequestInfo().getUserInfo().getUuid() : "system");
                logEntry.setLastmodifiedtime(Instant.now().toEpochMilli());
                logRepository.save(logEntry);
            }

            throw new RuntimeException("Failed to process polygon file: " + e.getMessage(), e);
        }
    }

    /**
     * Validates the uploaded polygon file
     */
    private void validatePolygonFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 10MB");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.toLowerCase().endsWith(".kml") && !fileName.toLowerCase().endsWith(".xml"))) {
            throw new IllegalArgumentException("File must be a KML or XML file");
        }
    }

    /**
     * Parses KML file to extract polygon geometry
     */
    private Polygon parseKmlFile(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            return KmlParser.parsePolygon(inputStream);
        } catch (Exception e) {
            log.error("Failed to parse KML file: {}", e.getMessage());
            throw new Exception("Invalid KML file format: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts district information from WFS response
     */
    private String extractDistrict(JsonNode wfsResponse) {
        if (wfsResponse != null && wfsResponse.has("features")) {
            JsonNode features = wfsResponse.get("features");
            if (features.isArray() && !features.isEmpty()) {
                JsonNode firstFeature = features.get(0);
                if (firstFeature.has("properties")) {
                    JsonNode properties = firstFeature.get("properties");
                    if (properties.has(gisProperties.getWfsDistrictAttribute())) {
                        return properties.get(gisProperties.getWfsDistrictAttribute()).asText();
                    }
                }
            }
        }
        return "Unknown";
    }

    /**
     * Extracts zone information from WFS response
     */
    private String extractZone(JsonNode wfsResponse) {
        if (wfsResponse != null && wfsResponse.has("features")) {
            JsonNode features = wfsResponse.get("features");
            if (features.isArray() && !features.isEmpty()) {
                JsonNode firstFeature = features.get(0);
                if (firstFeature.has("properties")) {
                    JsonNode properties = firstFeature.get("properties");
                    if (properties.has(gisProperties.getWfsZoneAttribute())) {
                        return properties.get(gisProperties.getWfsZoneAttribute()).asText();
                    }
                }
            }
        }
        return "Unknown";
    }

    /**
     * Cleans WFS response for client consumption - only essential fields
     */
    private JsonNode cleanWfsResponse(JsonNode wfsResponse) {
        if (wfsResponse == null) {
            return objectMapper.createObjectNode();
        }
        
        // Create a simplified response with only essential information
        ObjectNode cleanResponse = objectMapper.createObjectNode();
        cleanResponse.put("type", "FeatureCollection");

        if (wfsResponse.has("features")) {
        ArrayNode cleanFeatures = objectMapper.createArrayNode();
            JsonNode features = wfsResponse.get("features");

            if (features.isArray()) {
            for (JsonNode feature : features) {
                ObjectNode cleanFeature = objectMapper.createObjectNode();
                cleanFeature.put("type", "Feature");

                    // Only include essential properties, exclude demographic and geometric data
                    if (feature.has("properties")) {
                        JsonNode properties = feature.get("properties");
                ObjectNode cleanProperties = objectMapper.createObjectNode();
                        
                        // Only include district and zone related fields
                        if (properties.has("STATE_NAME")) {
                            cleanProperties.put("STATE_NAME", properties.get("STATE_NAME").asText());
                        }
                        if (properties.has("STATE_ABBR")) {
                            cleanProperties.put("STATE_ABBR", properties.get("STATE_ABBR").asText());
                        }
                        if (properties.has("STATE_FIPS")) {
                            cleanProperties.put("STATE_FIPS", properties.get("STATE_FIPS").asText());
                }

                cleanFeature.set("properties", cleanProperties);
                    }
                    
                    // Exclude geometry object entirely as requested
                    // cleanFeature.set("geometry", null);

                cleanFeatures.add(cleanFeature);
            }
        }

        cleanResponse.set("features", cleanFeatures);
        }
        
        return cleanResponse;
    }

    /**
     * Creates a GIS log entry
     */
    private GisLog createGisLog(String applicationNo, String rtpiId, String fileStoreId, String tenantId, 
                               String status, String responseStatus, String responseJson, JsonNode details, String createdBy) {
        return GisLog.builder()
                .applicationNo(applicationNo)
                .rtpiId(rtpiId)
                .fileStoreId(fileStoreId)
                .tenantId(tenantId)
                .status(status)
                .responseStatus(responseStatus)
                .responseJson(responseJson)
                .createdby(createdBy)
                .createdtime(Instant.now().toEpochMilli())
                .details(details)
                .build();
    }
}