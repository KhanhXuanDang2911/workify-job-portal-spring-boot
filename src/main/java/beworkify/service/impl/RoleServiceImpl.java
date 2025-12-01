package beworkify.service.impl;

import beworkify.dto.request.RoleRequest;
import beworkify.dto.response.RoleResponse;
import beworkify.entity.Role;
import beworkify.enumeration.UserRole;
import beworkify.exception.ResourceConflictException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.RoleMapper;
import beworkify.repository.RoleRepository;
import beworkify.service.RoleService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the RoleService interface. Handles business logic for user roles, including
 * creation, updates, and retrieval.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;
  private final RoleMapper roleMapper;
  private final MessageSource messageSource;

  @Override
  @CacheEvict(value = "roles", allEntries = true)
  public RoleResponse createRole(RoleRequest request) {

    if (roleRepository.existsByRole(UserRole.getRoleFromName(request.getRole()))) {
      log.error("Role with name '{}' already exists", request.getRole());
      String message =
          messageSource.getMessage(
              "role.exists.with.name",
              new Object[] {request.getRole()},
              LocaleContextHolder.getLocale());
      throw new ResourceConflictException(message);
    }

    Role role = roleMapper.toEntity(request);
    role.setRole(UserRole.getRoleFromName(request.getRole()));
    roleRepository.save(role);

    return roleMapper.toDTO(role);
  }

  @Override
  @CacheEvict(value = "roles", allEntries = true)
  public RoleResponse updateRole(RoleRequest request, Long id) {

    if (roleRepository.existsByRoleExceptForId(UserRole.getRoleFromName(request.getRole()), id)) {
      log.error("Role with name '{}' already exists (conflict)", request.getRole());
      String message =
          messageSource.getMessage(
              "role.exists.with.name",
              new Object[] {request.getRole()},
              LocaleContextHolder.getLocale());
      throw new ResourceConflictException(message);
    }

    Role role = findRoleById(id);
    roleMapper.updateEntityFromDTO(request, role);
    role.setRole(UserRole.getRoleFromName(request.getRole()));
    roleRepository.save(role);

    return roleMapper.toDTO(role);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "roles", key = "'all'")
  public List<RoleResponse> getAllRoles() {

    List<Role> roles = roleRepository.findAllRoles();
    List<RoleResponse> responses =
        roles.stream()
            .map(
                r ->
                    RoleResponse.builder()
                        .id(r.getId())
                        .role(r.getRole())
                        .description(r.getDescription())
                        .createdAt(r.getCreatedAt())
                        .updatedAt(r.getUpdatedAt())
                        .build())
            .collect(Collectors.toList());

    return responses;
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "roles", key = "#roleName")
  public RoleResponse getRoleByRoleName(String roleName) {

    Role role = findRoleByRoleName(roleName);

    return RoleResponse.builder()
        .id(role.getId())
        .role(role.getRole())
        .description(role.getDescription())
        .createdAt(role.getCreatedAt())
        .updatedAt(role.getUpdatedAt())
        .build();
  }

  @Override
  @CacheEvict(value = "roles", allEntries = true)
  public void deleteRole(Long id) {

    Role role = findRoleById(id);
    roleRepository.delete(role);
  }

  @Override
  public Role findRoleById(Long id) {

    return roleRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.error("Role with id = {} not found", id);
              return new ResourceNotFoundException(
                  String.format("Role with id = %d not found", id));
            });
  }

  @Override
  public Role findRoleByRoleName(String roleName) {

    UserRole userRole = UserRole.getRoleFromName(roleName);
    return roleRepository
        .findByRole(userRole)
        .orElseThrow(
            () -> {
              log.error("Role with role name = {} not found", userRole.getName());
              return new ResourceNotFoundException(
                  String.format("Role with role name = %s not found", userRole.getName()));
            });
  }
}
