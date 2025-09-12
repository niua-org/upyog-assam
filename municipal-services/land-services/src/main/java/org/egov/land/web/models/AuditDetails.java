package org.egov.land.web.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * Collection of audit related fields used by most models
 */
@ApiModel(description = "Collection of audit related fields used by most models")
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuditDetails {

  @ApiModelProperty(value = "Username (preferred) or user ID of the user that created the object.")
  private String createdBy;

  @ApiModelProperty(value = "Username (preferred) or user ID of the user that last modified the object.")
  private String lastModifiedBy;

  @ApiModelProperty(value = "Epoch of the time the object was created.")
  private Long createdTime;

  @ApiModelProperty(value = "Epoch of the time the object was last modified.")
  private Long lastModifiedTime;
}