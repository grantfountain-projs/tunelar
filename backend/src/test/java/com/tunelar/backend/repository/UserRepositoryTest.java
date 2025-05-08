package com.tunelar.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import com.tunelar.backend.model.Role;
import com.tunelar.backend.model.User;

/**
 * Tests for UserRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY) // Use embedded database
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    private User testUser;
    
    @BeforeEach
    public void setup() {
        // Create a role
        Role role = new Role();
        role.setName("ROLE_PROD");
        role = roleRepository.save(role);
        
        // Create a test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setName("Test User");
        testUser.setCreatedAt(LocalDateTime.now());
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        testUser.setRoles(roles);
        
        // Save the test user
        testUser = userRepository.save(testUser);
    }
    
    @Test
    public void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Given - user created in setup
        
        // When
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    public void findByEmail_ShouldReturnUser_WhenUserExists() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }
    
    @Test
    public void findByUsernameOrEmail_ShouldReturnUser_WhenSearchingByUsername() {
        // When
        Optional<User> foundUser = userRepository.findByUsernameOrEmail("testuser", "wrongemail@example.com");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }
    
    @Test
    public void findByUsernameOrEmail_ShouldReturnUser_WhenSearchingByEmail() {
        // When
        Optional<User> foundUser = userRepository.findByUsernameOrEmail("wrongusername", "test@example.com");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    public void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // When
        boolean exists = userRepository.existsByUsername("testuser");
        
        // Then
        assertThat(exists).isTrue();
    }
    
    @Test
    public void existsByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistentuser");
        
        // Then
        assertThat(exists).isFalse();
    }
    
    @Test
    public void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // When
        boolean exists = userRepository.existsByEmail("test@example.com");
        
        // Then
        assertThat(exists).isTrue();
    }
    
    @Test
    public void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        
        // Then
        assertThat(exists).isFalse();
    }
}