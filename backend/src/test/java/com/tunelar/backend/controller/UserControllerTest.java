package com.tunelar.backend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.tunelar.backend.config.MockMvcTestConfig;
import com.tunelar.backend.model.Role;
import com.tunelar.backend.model.User;
import com.tunelar.backend.service.AuthService;

@WebMvcTest(UserController.class)
@Import(MockMvcTestConfig.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    private User testUser;
    private Role testRole;
    
    @BeforeEach
    public void setup() {
        // Create test role
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("ROLE_PROD");
        
        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("jestes");
        testUser.setEmail("vitae.erat@yahoo.edu");
        testUser.setName("Jordan Estes");
        testUser.setPassword("encodedPassword");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setRoles(new HashSet<>(Collections.singletonList(testRole)));
        
        // Clear any previous mock interactions
        reset(authService);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        // Given - Important: Make sure this is explicitly returning testUser
        when(authService.getUserById(1L)).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(get("/api/user/byId/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()) // Add this to see the actual response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jestes"))
                .andExpect(jsonPath("$.name").value("Jordan Estes"))
                .andExpect(jsonPath("$.email").value("vitae.erat@yahoo.edu"));
                
        // Verify the service was called
        verify(authService).getUserById(1L);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUserByUsername_ShouldReturnUser_WhenUserExists() throws Exception {
        // Given
        when(authService.getUserByUsername("jestes")).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(get("/api/user/byUsername/jestes")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jestes"))
                .andExpect(jsonPath("$.name").value("Jordan Estes"))
                .andExpect(jsonPath("$.email").value("vitae.erat@yahoo.edu"));
                
        // Verify the service was called
        verify(authService).getUserByUsername("jestes");
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllUsers_ShouldReturnUserList() throws Exception {
        // Given
        List<User> userList = new ArrayList<>();
        userList.add(testUser);
        
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@tunelar.com");
        adminUser.setName("Admin User");
        adminUser.setCreatedAt(LocalDateTime.now());
        
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ROLE_ADMIN");
        adminUser.setRoles(new HashSet<>(Collections.singletonList(adminRole)));
        
        userList.add(adminUser);
        
        when(authService.getAllUsers()).thenReturn(userList);
        
        // When & Then
        mockMvc.perform(get("/api/user/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("jestes"))
                .andExpect(jsonPath("$[1].username").value("admin"));
                
        // Verify the service was called
        verify(authService).getAllUsers();
    }
    
    @Test
    @WithMockUser(roles = "MOD")
    public void getUserById_ShouldReturnUser_WhenModRequests() throws Exception {
        // Given
        when(authService.getUserById(1L)).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(get("/api/user/byId/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jestes"));
                
        // Verify the service was called
        verify(authService).getUserById(1L);
    }
    
    @Test
    public void getUserById_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        // We need to ensure security filter chain is enforcing authentication
        mockMvc.perform(get("/api/user/byId/1")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }
}