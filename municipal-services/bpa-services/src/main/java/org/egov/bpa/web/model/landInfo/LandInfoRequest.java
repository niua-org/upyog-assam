package org.egov.bpa.web.model.landInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * Contract class to receive request. Array of Property items are used in case of create. Whereas single Property item is used for update.
 */
@ApiModel(description = "Contract class to receive request. Array of Property items are used in case of create. Whereas single Property item is used for update.")
@Validated
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LandInfoRequest {

  @ApiModelProperty(value = "Request information for the API call.")
  @Valid
  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;

  @ApiModelProperty(value = "Land information details.")
  @Valid
  private LandInfo landInfo;
}