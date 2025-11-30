package beworkify.service;

import beworkify.dto.request.RoleRequest;
import beworkify.dto.response.RoleResponse;
import beworkify.entity.Role;
import java.util.List;

public interface RoleService {
  RoleResponse createRole(RoleRequest request);

  RoleResponse updateRole(RoleRequest request, Long id);

  List<RoleResponse> getAllRoles();

  RoleResponse getRoleByRoleName(String roleName);

  Role findRoleById(Long id);

  Role findRoleByRoleName(String roleName);

  public void deleteRole(Long id);
}
