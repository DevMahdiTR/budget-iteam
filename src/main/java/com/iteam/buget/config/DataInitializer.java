package com.iteam.buget.config;

import com.iteam.buget.core.category.Category;
import com.iteam.buget.core.category.CategoryRepository;
import com.iteam.buget.core.role.Role;
import com.iteam.buget.core.role.RoleName;
import com.iteam.buget.core.role.RoleRepository;
import com.iteam.buget.core.user.User;
import com.iteam.buget.core.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedAdminUser();
        seedDefaultCategories();
    }

    private void seedRoles() {
        for (RoleName name : RoleName.values()) {
            if (roleRepository.findByRoleName(name).isEmpty()) {
                roleRepository.save(new Role(null, name));
                log.info("Created role: {}", name);
            }
        }
    }

    private void seedAdminUser() {
        if (userRepository.existsByEmail("admin@budget.app")) return;

        Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                .orElseThrow();

        User admin = User.builder()
                .firstName("Admin")
                .lastName("System")
                .email("admin@budget.app")
                .password(passwordEncoder.encode("Admin@1234"))
                .enabled(true)
                .accountValidated(true)
                .deletionRequested(false)
                .role(adminRole)
                .build();

        userRepository.save(admin);
        log.info("Created admin user: admin@budget.app / Admin@1234");
    }

    private void seedDefaultCategories() {
        if (!categoryRepository.findByIsDefaultTrue().isEmpty()) return;

        List<String> defaults = List.of(
                "Alimentation", "Transport", "Logement", "Santé",
                "Loisirs", "Études", "Épargne", "Salaire", "Autre"
        );

        defaults.forEach(name -> {
            categoryRepository.save(Category.builder()
                    .name(name)
                    .isDefault(true)
                    .createdBy(null)
                    .build());
            log.info("Created default category: {}", name);
        });
    }
}