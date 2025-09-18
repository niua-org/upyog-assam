package org.upyog.gis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
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
    @Operation(summary = "Process polygon file", description = "Uploads and processes a polygon KML file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PostMapping(value = "/process-polygon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PolygonProcessingResponse> processPolygon(
            @Parameter(description = "Polygon KML file", required = true)
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Tenant ID", required = true)
            @RequestParam String tenantId,
            @Parameter(description = "Application Number", required = true)
            @RequestParam String applicationNo,
            @Parameter(description = "RTPI ID", required = true)
            @RequestParam String rtpiId
    ) {

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