package beworkify.configuration.initializer;

import beworkify.dto.request.EmployerRequest;
import beworkify.dto.request.RoleRequest;
import beworkify.dto.request.UserRequest;
import beworkify.enumeration.LevelCompanySize;
import beworkify.enumeration.StatusUser;
import beworkify.enumeration.UserRole;
import beworkify.repository.EmployerRepository;
import beworkify.repository.RoleRepository;
import beworkify.repository.UserRepository;
import beworkify.service.EmployerService;
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
    private final EmployerRepository employerRepository;
    private final EmployerService employerService;

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
        if (!employerRepository.existsByEmail("employer@example.com")) {
            employerService.createEmployer(EmployerRequest.builder()
                    .companyName("Company Example")
                    .email("employer@example.com")
                    .password("Employer@123")
                    .companySize(LevelCompanySize.FROM_100_TO_499.getLabel())
                    .contactPerson("Company HR")
                    .phoneNumber("0123456789")
                    .provinceId(1L)
                    .districtId(1L)
                    .aboutCompany("Company Example")
                    .detailAddress("123 Example Street")
                    .status(StatusUser.ACTIVE.getName())
                    .build(), null, null);
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
