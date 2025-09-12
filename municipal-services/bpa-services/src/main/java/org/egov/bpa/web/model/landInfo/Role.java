package org.egov.bpa.web.model.landInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Minimal representation of the Roles in the system to be carried along in UserInfo with RequestInfo meta data. Actual authorization service to extend this to have more role-related attributes.
 */
@ApiModel(description = "Minimal representation of the Roles in the system to be carried along in UserInfo with RequestInfo meta data. Actual authorization service to extend this to have more role-related attributes.")
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class Role {

  @ApiModelProperty(required = true, value = "Unique name of the role.")
  @SafeHtml
  @NotNull
  @Size(max = 64)
  private String name;

  @ApiModelProperty(value = "Unique code of the role.")
  @SafeHtml
  @Size(max = 64)
  private String code;

  @ApiModelProperty(value = "Brief description of the role.")
  @SafeHtml
  private String description;
}