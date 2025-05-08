package com.tunelar.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.tunelar.backend.model.Role;

/**
 * Tests for RoleRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // Use the configured database
@ActiveProfiles("ci")
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;
    
    @Test
    public void findByName_ShouldReturnRole_WhenRoleExists() {
        // Given
        Role role = new Role();
        role.setName("ROLE_TEST");
        roleRepository.save(role);
        
        // When
        Role foundRole = roleRepository.findByName("ROLE_TEST");
        
        // Then
        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getName()).isEqualTo("ROLE_TEST");
    }
    
    @Test
    public void findByName_ShouldReturnNull_WhenRoleDoesNotExist() {
        // When
        Role foundRole = roleRepository.findByName("ROLE_NONEXISTENT");
        
        // Then
        assertThat(foundRole).isNull();
    }
    
    @Test
    public void save_ShouldCreateRole() {
        // Given
        Role role = new Role();
        role.setName("ROLE_SAVE_TEST");
        role.setDescription("Test role for save method");
        
        // When
        Role savedRole = roleRepository.save(role);
        
        // Then
        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("ROLE_SAVE_TEST");
        assertThat(savedRole.getDescription()).isEqualTo("Test role for save method");
    }
    
    @Test
    public void save_ShouldUpdateExistingRole() {
        // Given
        Role role = new Role();
        role.setName("ROLE_UPDATE_TEST");
        role = roleRepository.save(role);
        
        // When
        role.setDescription("Updated description");
        Role updatedRole = roleRepository.save(role);
        
        // Then
        assertThat(updatedRole).isNotNull();
        assertThat(updatedRole.getId()).isEqualTo(role.getId());
        assertThat(updatedRole.getDescription()).isEqualTo("Updated description");
    }
}