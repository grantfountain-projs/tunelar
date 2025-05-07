package com.tunelar.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tunelar.backend.model.Role;
import com.tunelar.backend.repository.RoleRepository;

/**
 * Initializes the database with default roles.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private RoleRepository roleRepository;

    public DatabaseInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize admin role
        createRoleIfNotExists(Roles.ROLE_ADMIN);
        
        // Initialize other user roles
        for (Roles.UserRoles role : Roles.UserRoles.values()) {
            createRoleIfNotExists(role.name());
        }
    }
    
    /**
     * Creates a role if it doesn't already exist
     * 
     * @param roleName the name of the role to create
     */
    private void createRoleIfNotExists(String roleName) {
        Role existingRole = roleRepository.findByName(roleName);
        if (existingRole == null) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}