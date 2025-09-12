package org.egov.bpa.web.model.landInfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

/**
 * GeoLocation
 */
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GeoLocation {

  @ApiModelProperty(value = "Unique identifier of the GeoLocation.")
  @SafeHtml
  private String id;

  @ApiModelProperty(value = "Latitude of the address.")
  private Double latitude;

  @ApiModelProperty(value = "Longitude of the address.")
  private Double longitude;

  @ApiModelProperty(value = "JSON object to capture any extra information which is not accommodated by the model.")
  private Object additionalDetails;
}