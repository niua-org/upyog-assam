package org.upyog.gis.wfs.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.upyog.gis.config.GisProperties;
import org.upyog.gis.wfs.WfsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * HTTP-based WFS client implementation with robust error handling and axis swapping
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpWfsClient implements WfsClient {

    private final WebClient webClient;
    private final GisProperties gisProperties;

    @Override
    public JsonNode queryFeatures(String polygonWkt) throws Exception {
        String geomAttr = Optional.ofNullable(gisProperties.getWfsGeometryColumn()).orElse("the_geom");
        String typeName = gisProperties.getWfsTypeName();
        String baseUrl = gisProperties.getWfsUrl();

        // build and execute one attempt; if empty, swap axes and retry once
        JsonNode result = executeWfsQuery(baseUrl, typeName, geomAttr, polygonWkt);
        if (isEmptyFeatureCollection(result)) {
            String swapped = swapWktAxes(polygonWkt);
            log.info("WFS returned empty for original WKT; retrying with swapped axes WKT: {}", swapped);
            result = executeWfsQuery(baseUrl, typeName, geomAttr, swapped);
        }
        return result;
    }

    private JsonNode executeWfsQuery(String baseUrl, String typeName, String geomAttr, String polygonWkt) {
        try {
            String cql = String.format("INTERSECTS(%s, %s)", geomAttr, polygonWkt);
            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("service", "WFS")
                    .queryParam("version", "2.0.0")
                    .queryParam("request", "GetFeature")
                    .queryParam("typeName", typeName)
                    .queryParam("outputFormat", "application/json")
                    .queryParam("srsName", "EPSG:4326")          // explicit CRS
                    .queryParam("cql_filter", cql)
                    .build()
                    .encode()
                    .toUri();

            log.info("Calling WFS: {}", uri);
            log.info("CQL Filter: {}", cql);

            String body = webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.isError(), resp -> resp.bodyToMono(String.class)
                    .flatMap(msg -> Mono.error(new RuntimeException("WFS error: " + resp.statusCode() + " - " + msg))))
                .bodyToMono(String.class)
                .block();

            return new ObjectMapper().readTree(body);
        } catch (WebClientResponseException w) {
            throw new RuntimeException("WFS query failed: " + w.getRawStatusCode() + " " + w.getMessage(), w);
        } catch (Exception e) {
            throw new RuntimeException("WFS query failed: " + e.getMessage(), e);
        }
    }

    private boolean isEmptyFeatureCollection(JsonNode node) {
        if (node == null) return true;
        JsonNode features = node.get("features");
        if (features != null && features.isArray() && features.size() > 0) return false;
        JsonNode total = node.get("totalFeatures");
        if (total != null && total.asInt(0) > 0) return false;
        return true;
    }

    private String swapWktAxes(String wkt) {
        // naive swap: assume "POLYGON((x1 y1,x2 y2,...))"
        // will transform to "POLYGON((y1 x1,y2 x2,...))"
        String inner = wkt.replaceAll("^.*\\(\\(", "").replaceAll("\\)\\).*$", "");
        String[] pts = inner.split(",");
        List<String> swapped = new ArrayList<>();
        for (String p : pts) {
            String[] xy = p.trim().split("\\s+");
            if (xy.length >= 2) swapped.add(xy[1] + " " + xy[0]);
        }
        // ensure ring closed
        if (!swapped.get(0).equals(swapped.get(swapped.size()-1))) swapped.add(swapped.get(0));
        return "POLYGON((" + String.join(",", swapped) + "))";
    }

}
