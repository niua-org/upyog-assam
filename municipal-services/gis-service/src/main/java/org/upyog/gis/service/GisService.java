package org.upyog.gis.service;

import org.upyog.gis.model.GISResponse;
import org.upyog.gis.model.GISRequestWrapper;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for GIS operations
 */
public interface GisService {

    /**
     * Find zone information from polygon file
     *
     * @param file the polygon file (KML)
     * @param gisRequestWrapper the GIS request wrapper containing RequestInfo and GIS request data
     * @return response containing district, zone, and WFS response
     * @throws Exception if processing fails
     */
    GISResponse findZoneFromPolygon(MultipartFile file, GISRequestWrapper gisRequestWrapper) throws Exception;
}
