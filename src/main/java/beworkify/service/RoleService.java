package beworkify.service;

import beworkify.dto.request.RoleRequest;
import beworkify.dto.response.RoleResponse;
import beworkify.entity.Role;
import java.util.List;

/**
 * Service interface for managing user roles. Provides business logic for CRUD operations on role
 * data.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public interface RoleService {
  RoleResponse createRole(RoleRequest request);

  RoleResponse updateRole(RoleRequest request, Long id);

  List<RoleResponse> getAllRoles();

  RoleResponse getRoleByRoleName(String roleName);

  Role findRoleById(Long id);

  Role findRoleByRoleName(String roleName);

  public void deleteRole(Long id);
}
