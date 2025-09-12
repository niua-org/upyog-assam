package org.egov.bpa.web.model.landInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * Contains the ResponseHeader and the created/updated property
 */
@ApiModel(description = "Contains the ResponseHeader and the created/updated property")
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LandInfoResponse {

  @ApiModelProperty(value = "Response information for the API call.")
  @Valid
  @JsonProperty("ResponseInfo")
  private ResponseInfo responseInfo;

  @ApiModelProperty(value = "List of land information details.")
  @Valid
  @JsonProperty("LandInfo")
  private List<LandInfo> landInfo;

}