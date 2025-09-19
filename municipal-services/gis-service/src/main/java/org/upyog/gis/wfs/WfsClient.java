package org.upyog.gis.wfs;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Client interface for WFS (Web Feature Service) operations
 */
public interface WfsClient {

    /**
     * Query WFS service for features that intersect with the given polygon
     *
     * @param polygonWkt the polygon in WKT format
     * @return WFS response as JsonNode containing matching features
     * @throws Exception if query fails
     */
    JsonNode queryFeatures(String polygonWkt) throws Exception;
}
