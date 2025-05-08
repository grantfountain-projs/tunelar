package com.tunelar.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tunelar.backend.dto.JwtAuthResponse;
import com.tunelar.backend.dto.LoginDto;
import com.tunelar.backend.dto.RegisterDto;
import com.tunelar.backend.exception.TunelarAPIException;
import com.tunelar.backend.model.Role;
import com.tunelar.backend.model.User;
import com.tunelar.backend.repository.RoleRepository;
import com.tunelar.backend.repository.UserRepository;
import com.tunelar.backend.security.JwtTokenProvider;
import com.tunelar.backend.service.impl.AuthServiceImpl;

/**
 * Tests for AuthService implementation.
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    private RegisterDto registerDto;
    private LoginDto loginDto;
    private User user;
    private Role role;
    
    @BeforeEach
    public void setup() {
        // Set up RegisterDto
        registerDto = new RegisterDto();
        registerDto.setName("Test User");
        registerDto.setUsername("testuser");
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password");
        
        // Set up LoginDto
        loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("testuser");
        loginDto.setPassword("password");
        
        // Set up Role
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_PROD");
        
        // Set up User
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(new HashSet<>());
        user.getRoles().add(role);
    }
    
    @Test
    public void register_ShouldReturnSuccessMessage_WhenValidDataProvided() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("ROLE_PROD")).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // When
        String result = authService.register(registerDto);
        
        // Then
        assertThat(result).isEqualTo("User registered successfully.");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    public void register_ShouldThrowException_WhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // When & Then
        assertThrows(TunelarAPIException.class, () -> {
            authService.register(registerDto);
        });
    }
    
    @Test
    public void register_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // When & Then
        assertThrows(TunelarAPIException.class, () -> {
            authService.register(registerDto);
        });
    }
    
    @Test
    public void login_ShouldReturnJwtAuthResponse_WhenValidCredentialsProvided() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));
        
        // When
        JwtAuthResponse response = authService.login(loginDto);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getRole()).isEqualTo("ROLE_PROD");
    }
    
    @Test
    public void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        // When
        User result = authService.getUserById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }
    
    @Test
    public void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        
        // When
        User result = authService.getUserByUsername("testuser");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }
    
    @Test
    public void updateUserRole_ShouldReturnSuccessMessage_WhenValidDataProvided() {
        // Given
        // Create a user with ID 2 (not admin which is ID 1)
        User nonAdminUser = new User();
        nonAdminUser.setId(2L);
        nonAdminUser.setUsername("testuser");
        nonAdminUser.setRoles(new HashSet<>());
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(nonAdminUser));
        when(roleRepository.findByName("ROLE_MOD")).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(nonAdminUser);
        
        // When
        String result = authService.updateUserRole(2L, "ROLE_MOD");
        
        // Then
        assertThat(result).isEqualTo("User role updated successfully.");
        verify(userRepository).save(any(User.class));
    }
}