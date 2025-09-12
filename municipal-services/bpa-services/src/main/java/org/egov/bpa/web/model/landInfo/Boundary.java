package org.egov.bpa.web.model.landInfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Boundary
 */
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Boundary {

  @ApiModelProperty(required = true, value = "Code of the boundary.")
  @NotNull
  @SafeHtml
  private String code;

  @ApiModelProperty(required = true, value = "Name of the boundary.")
  @NotNull
  @SafeHtml
  private String name;

  @ApiModelProperty(value = "Localized label for the boundary.")
  @SafeHtml
  private String label;

  @ApiModelProperty(value = "Latitude of the boundary.")
  @SafeHtml
  private String latitude;

  @ApiModelProperty(value = "Longitude of the boundary.")
  @SafeHtml
  private String longitude;

  @ApiModelProperty(value = "List of child boundaries.")
  @Valid
  private List<Boundary> children;

  @ApiModelProperty(readOnly = true, value = "Materialized path of the boundary - this would be of the format tenantid.[code] from parent till the current boundary.")
  @SafeHtml
  private String materializedPath;
}