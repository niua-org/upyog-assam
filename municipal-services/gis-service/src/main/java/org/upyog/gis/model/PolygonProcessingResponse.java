package org.upyog.gis.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for polygon processing endpoint
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolygonProcessingResponse {

    private String district;
    private String zone;
    private Object wfsResponse;
    private String fileStoreId;
    private String error;
}
