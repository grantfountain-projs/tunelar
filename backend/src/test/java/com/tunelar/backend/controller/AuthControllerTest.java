package com.tunelar.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tunelar.backend.config.MockMvcTestConfig;
import com.tunelar.backend.dto.JwtAuthResponse;
import com.tunelar.backend.dto.LoginDto;
import com.tunelar.backend.dto.RegisterDto;
import com.tunelar.backend.dto.RoleUpdateDto;
import com.tunelar.backend.dto.UsernameUpdateDto;
import com.tunelar.backend.service.AuthService;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@Import(MockMvcTestConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLoginAdmin() throws Exception {
        // Prepare test data
        LoginDto loginDto = new LoginDto("admin", "admin123");
        JwtAuthResponse response = new JwtAuthResponse("test-token", "Bearer", "ROLE_ADMIN");
        
        when(authService.login(any(LoginDto.class))).thenReturn(response);

        // Perform request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    public void testCreateProdAndLogin() throws Exception {
        RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC");
        
        when(authService.register(any(RegisterDto.class))).thenReturn("User registered successfully.");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully."));
        
        // Test login
        LoginDto loginDto = new LoginDto("jestes", "JXB16TBD4LC");
        JwtAuthResponse response = new JwtAuthResponse("test-token", "Bearer", "ROLE_PROD");
        
        when(authService.login(any(LoginDto.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ROLE_PROD"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserRoleToModAndUpdateUsername() throws Exception {
        RoleUpdateDto roleUpdateDto = new RoleUpdateDto("ROLE_MOD");
        
        when(authService.updateUserRole(1L, "ROLE_MOD")).thenReturn("User role updated successfully.");
        
        mockMvc.perform(put("/api/auth/user/role/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User role updated successfully."));
        
        UsernameUpdateDto usernameUpdateDto = new UsernameUpdateDto("john");
        
        when(authService.updateUserUsername(1L, "john")).thenReturn("User username updated successfully.");
        
        mockMvc.perform(put("/api/auth/user/username/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usernameUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User username updated successfully."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteProducer() throws Exception {
        doNothing().when(authService).deleteUserById(1L);
        
        mockMvc.perform(delete("/api/auth/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully."));
    }
}