package org.upyog.gis.controller;

import org.upyog.gis.model.PolygonProcessingResponse;
import org.upyog.gis.service.GisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for GIS operations
 */
@Slf4j
@RestController
@RequestMapping("/api/gis")
@RequiredArgsConstructor
public class GisController {

    private final GisService gisService;

    /**
     * Process polygon file and return district/zone information
     *
     * @param file the polygon file to process
     * @param tenantId optional tenant ID
     * @param applicationNo optional application number
     * @param rtpiId optional RTPI ID
     * @return response containing district, zone, and WFS response
     */
    @PostMapping("/process-polygon")
    public ResponseEntity<PolygonProcessingResponse> processPolygonFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "tenantId", required = false) String tenantId,
            @RequestParam(value = "applicationNo", required = false) String applicationNo,
            @RequestParam(value = "rtpiId", required = false) String rtpiId) {

        try {
            log.info("Processing polygon file: {} (tenant: {}, applicationNo: {}, rtpiId: {})", 
                    file.getOriginalFilename(), tenantId, applicationNo, rtpiId);

            PolygonProcessingResponse response = gisService.processPolygonFile(file, tenantId, applicationNo, rtpiId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            PolygonProcessingResponse errorResponse = PolygonProcessingResponse.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            log.error("Error processing polygon file", e);
            PolygonProcessingResponse errorResponse = PolygonProcessingResponse.builder()
                    .error("Internal server error: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}