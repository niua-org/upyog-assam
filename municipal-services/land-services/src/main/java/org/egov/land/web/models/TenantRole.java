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
 * User role carries the tenant related role information for the user. A user can have multiple roles per tenant based on the need of the tenant. A user may also have multiple roles for multiple tenants.
 */
@ApiModel(description = "User role carries the tenant related role information for the user. A user can have multiple roles per tenant based on the need of the tenant. A user may also have multiple roles for multiple tenants.")
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TenantRole {

  @ApiModelProperty(required = true, value = "Tenant ID for the tenant.")
  @NotNull
  private String tenantId;

  @ApiModelProperty(required = true, value = "Roles assigned for a particular tenant - array of role codes/names.")
  @NotNull
  @Valid
  private List<Role> roles = new ArrayList<>();
}