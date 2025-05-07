package com.tunelar.backend.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tunelar.backend.model.Role;
import com.tunelar.backend.model.User;
import com.tunelar.backend.repository.RoleRepository;
import com.tunelar.backend.repository.UserRepository;

/**
 * Initializes test data for the test environment
 */
@Configuration
@Profile("test")
public class TestDataInitializer {

    @Value("${app.admin-user-password}")
    private String adminPassword;

    

    /**
     * Creates a CommandLineRunner that initializes test data
     * 
     * @param roleRepository Repository for roles
     * @param userRepository Repository for users
     * @param passwordEncoder Password encoder
     * @return CommandLineRunner
     */
    @Bean
    public CommandLineRunner initTestData(RoleRepository roleRepository, 
                                         UserRepository userRepository,
                                         PasswordEncoder passwordEncoder) {
        return args -> {
            // Create roles
            Role adminRole = createRoleIfNotExists(roleRepository, "ROLE_ADMIN");
            createRoleIfNotExists(roleRepository, "ROLE_MOD");
            createRoleIfNotExists(roleRepository, "ROLE_PROD");

            // Create admin user if it doesn't exist
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@tunelar.com");
                admin.setPassword(passwordEncoder.encode(adminPassword));
                
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);
                admin.setName("Admin User");
                userRepository.save(admin);
            }
        };
    }
    
    private Role createRoleIfNotExists(RoleRepository roleRepository, String roleName) {
        Role existingRole = roleRepository.findByName(roleName);
        if (existingRole == null) {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        }
        return existingRole;
    }
}