package beworkify.configuration;

import beworkify.dto.request.RoleRequest;
import beworkify.dto.request.UserRequest;
import beworkify.enumeration.StatusUser;
import beworkify.enumeration.UserRole;
import beworkify.repository.RoleRepository;
import beworkify.repository.UserRepository;
import beworkify.service.RoleService;
import beworkify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initAdminUser();
    }

    public void initAdminUser() {
        if (!userRepository.existsByEmail("admin@example.com")) {
            userService.createUser(UserRequest.builder()
                    .fullName("System Administrator")
                    .email("admin@example.com")
                    .password("Admin@123")
                    .role(UserRole.ADMIN.getName())
                    .status(StatusUser.ACTIVE.getName())
                    .build(), null);
        }
    }

    public void initRoles() {
        if (!roleRepository.existsByRole(UserRole.JOB_SEEKER)) {
            roleService.createRole(RoleRequest.builder()
                    .role(UserRole.JOB_SEEKER.getName())
                    .description("Default system user with basic access rights")
                    .build());
        }
        if (!roleRepository.existsByRole(UserRole.ADMIN)) {
            roleService.createRole(RoleRequest.builder()
                    .role(UserRole.ADMIN.getName())
                    .description("System administrator with full access and management privileges")
                    .build());
        }
    }

}
