package org.egov.land.web.models;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This object holds list of documents attached during the transaction for a property
 */
@ApiModel(description = "This object holds list of documents attached during the transaction for a property")
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Document {

  @ApiModelProperty(value = "System ID of the Document.")
  @SafeHtml
  @Size(max = 64)
  private String id;

  @ApiModelProperty(value = "Unique document type code, should be validated with document type master.")
  @SafeHtml
  private String documentType;

  @ApiModelProperty(value = "File store reference key.")
  @SafeHtml
  private String fileStoreId;

  @ApiModelProperty(value = "The unique ID (Pancard Number, Aadhaar, etc.) of the given Document.")
  @SafeHtml
  @Size(max = 64)
  private String documentUid;

  @ApiModelProperty(value = "JSON object to capture any extra information which is not accommodated by the model.")
  private Object additionalDetails;

  @ApiModelProperty(value = "Audit details of the document.")
  @Valid
  private AuditDetails auditDetails;
}