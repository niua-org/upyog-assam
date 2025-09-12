package org.egov.land.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is acting ID token of the authenticated user on the server. Any value provided by the clients will be ignored and actual user based on authtoken will be used on the server.
 */
@ApiModel(description = "This is acting ID token of the authenticated user on the server. Any value provided by the clients will be ignored and actual user based on authtoken will be used on the server.")
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfo {

  @ApiModelProperty(required = true, value = "Unique Identifier of the tenant to which user primarily belongs")
  @NotNull
  private String tenantId;

  @ApiModelProperty(value = "System Generated User id of the authenticated user.")
  private String uuid;

  @ApiModelProperty(required = true, value = "Unique user name of the authenticated user")
  @NotNull
  private String userName;

  @ApiModelProperty(value = "Password of the user.")
  private String password;

  @ApiModelProperty(value = "This will be the OTP.")
  private String idToken;

  @ApiModelProperty(value = "Mobile number of the authenticated user")
  private String mobile;

  @ApiModelProperty(value = "Email address of the authenticated user")
  private String email;

  @ApiModelProperty(required = true, value = "List of all the roles for the primary tenant")
  @NotNull
  @Valid
  private List<Role> primaryrole = new ArrayList<>();

  @ApiModelProperty(value = "Array of additional tenant IDs authorized for the authenticated user")
  @Valid
  private List<TenantRole> additionalroles = null;
}