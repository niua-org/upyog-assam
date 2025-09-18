package org.upyog.gis.service;

import org.upyog.gis.model.PolygonProcessingResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for GIS operations
 */
public interface GisService {

    /**
     * Process polygon file and return district/zone information
     *
     * @param file the polygon file (KML)
     * @param tenantId optional tenant ID
     * @param applicationNo optional application number
     * @param rtpiId optional RTPI ID
     * @return response containing district, zone, and WFS response
     * @throws Exception if processing fails
     */
    PolygonProcessingResponse processPolygonFile(MultipartFile file, String tenantId, String applicationNo, String rtpiId) throws Exception;
}
