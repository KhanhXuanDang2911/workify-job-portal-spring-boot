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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final MessageSource messageSource;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        log.info("Attempting to create new role: {}", request.getRole());

        if (roleRepository.existsByRole(UserRole.getRoleFromName(request.getRole()))) {
            log.error("Role with name '{}' already exists", request.getRole());
            String message = messageSource.getMessage("role.exists.with.name", new Object[] { request.getRole() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        Role role = roleMapper.toEntity(request);
        role.setRole(UserRole.getRoleFromName(request.getRole()));
        roleRepository.save(role);

        log.info("Successfully created role with id = {}, name = {}", role.getId(), role.getRole().getName());
        return roleMapper.toDTO(role);
    }

    @Override
    public RoleResponse updateRole(RoleRequest request, Long id) {
        log.info("Attempting to update role with id = {}", id);

        if (roleRepository.existsByRoleExceptForId(UserRole.getRoleFromName(request.getRole()), id)) {
            log.error("Role with name '{}' already exists (conflict)", request.getRole());
            String message = messageSource.getMessage("role.exists.with.name", new Object[] { request.getRole() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        Role role = findRoleById(id);
        roleMapper.updateEntityFromDTO(request, role);
        role.setRole(UserRole.getRoleFromName(request.getRole()));
        roleRepository.save(role);

        log.info("Successfully updated role with id = {}", id);
        return roleMapper.toDTO(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.info("Fetching all roles");

        List<Role> roles = roleRepository.findAllRoles();
        List<RoleResponse> responses = roles.stream()
                .map(r -> RoleResponse.builder()
                        .id(r.getId())
                        .role(r.getRole())
                        .description(r.getDescription())
                        .createdAt(r.getCreatedAt())
                        .updatedAt(r.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        log.info("Found {} roles", responses.size());
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleByRoleName(String roleName) {
        log.info("Fetching role by name: {}", roleName);

        Role role = findRoleByRoleName(roleName);

        log.info("Found role with id = {} for name = {}", role.getId(), roleName);

        return RoleResponse.builder()
                .id(role.getId())
                .role(role.getRole())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Attempting to delete role with id = {}", id);

        Role role = findRoleById(id);
        roleRepository.delete(role);

        log.info("Successfully deleted role with id = {}", id);
    }

    @Override
    public Role findRoleById(Long id) {
        log.debug("Looking up role by id = {}", id);

        return roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role with id = {} not found", id);
                    return new ResourceNotFoundException(
                            String.format("Role with id = %d not found", id));
                });
    }

    @Override
    public Role findRoleByRoleName(String roleName) {
        log.debug("Looking up role by roleName = {}", roleName);
        UserRole userRole = UserRole.getRoleFromName(roleName);
        return roleRepository.findByRole(userRole)
                .orElseThrow(() -> {
                    log.error("Role with role name = {} not found", userRole.getName());
                    return new ResourceNotFoundException(
                            String.format("Role with role name = %s not found", userRole.getName()));
                });
    }

}
