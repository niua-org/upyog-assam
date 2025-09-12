package org.egov.land.web.models;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Institution
 */
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Institution {

  @ApiModelProperty(value = "Unique Identifier of the Institution(UUID).")
  @SafeHtml
  @Size(max = 64)
  private String id;

  @ApiModelProperty(value = "Tenant ID of the Property.")
  @SafeHtml
  @Size(max = 256)
  private String tenantId;

  @ApiModelProperty(value = "Institution type.")
  @SafeHtml
  @Size(max = 64)
  private String type;

  @ApiModelProperty(value = "Designation of the person creating/updating entity on behalf of the institution.")
  @SafeHtml
  @Size(max = 64)
  private String designation;

  @ApiModelProperty(value = "Name of the person who is taking action on behalf of the institution.")
  @SafeHtml
  @Size(max = 256)
  private String nameOfAuthorizedPerson;

  @ApiModelProperty(value = "JSON object to capture any extra information which is not accommodated by the model.")
  private Object additionalDetails;
}