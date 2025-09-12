package org.egov.land.web.models;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Construction/constructionDetail details are captured here. Detail information of the constructionDetail including floor wise usage and area are saved as separate units. For each financial year construction details may change. constructionDetail object is required for tax calculation.
 */
@ApiModel(description = "Construction/constructionDetail details are captured here. Detail information of the constructionDetail including floor wise usage and area are saved as separate units. For each financial year construction details may change. constructionDetail object is required for tax calculation.")
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConstructionDetail {

  @ApiModelProperty(value = "ID of the property with which the constructionDetail is associated.")
  @Size(max = 64)
  private String id;

  @ApiModelProperty(value = "Total built up area in sq ft (built-up area = carpet area + areas covered by walls).")
  @Valid
  private BigDecimal carpetArea;

  @ApiModelProperty(value = "Total built up area in sq ft (built-up area = carpet area + areas covered by walls).")
  @Valid
  private BigDecimal builtUpArea;

  @ApiModelProperty(value = "Area of the extension built-up of the Unit, like balcony, sit-outs.")
  @Valid
  private BigDecimal plinthArea;

  @ApiModelProperty(value = "Total built up area in sq ft (built-up area + Common area = Super built-up area).")
  @Valid
  private BigDecimal superBuiltUpArea;

  @ApiModelProperty(value = "Construction type is defined in MDMS ConstructionTypeMaster.")
  @Size(min = 1, max = 64)
  private String constructionType;

  @ApiModelProperty(value = "The date when the property was constructed.")
  private Long constructionDate;

  @ApiModelProperty(value = "The dimensions of the plot or building or any unit.")
  private Object dimensions;

  @ApiModelProperty(value = "Audit details of the construction.")
  @Valid
  private AuditDetails auditDetails;

  @ApiModelProperty(value = "Additional details in JSON format.")
  private Object additionalDetails;
}