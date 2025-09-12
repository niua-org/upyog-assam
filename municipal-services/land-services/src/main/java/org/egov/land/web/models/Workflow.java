package org.egov.land.web.models;

import java.util.ArrayList;
import java.util.List;

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
 * BPA application object to capture the details of land, land owners, and address of the land.
 */
@ApiModel(description = "BPA application object to capture the details of land, land owners, and address of the land.")
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Workflow {

  @ApiModelProperty(value = "Action on the application in certain")
  @Size(min = 1, max = 64)
  private String action;

  @ApiModelProperty(value = "List of assignees for the workflow")
  @Valid
  private List<String> assignes = new ArrayList<>();

  @ApiModelProperty(value = "Unique Identifier scrutinized number")
  @Size(min = 1, max = 64)
  private String comments;

  @ApiModelProperty(value = "Attach the workflow verification documents.")
  @Valid
  private List<Document> varificationDocuments = new ArrayList<>();
}