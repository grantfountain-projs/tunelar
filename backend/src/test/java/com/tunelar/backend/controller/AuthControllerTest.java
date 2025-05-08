package com.tunelar.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

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
    
    @BeforeEach
    public void setup() {
        // Reset mocks before each test
        reset(authService);
    }

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
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
                
        verify(authService).login(any(LoginDto.class));
    }

    @Test
    public void testCreateProdAndLogin() throws Exception {
        RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC");
        
        when(authService.register(any(RegisterDto.class))).thenReturn("User registered successfully.");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully."));
        
        verify(authService).register(any(RegisterDto.class));
        
        // Test login
        LoginDto loginDto = new LoginDto("jestes", "JXB16TBD4LC");
        JwtAuthResponse response = new JwtAuthResponse("test-token", "Bearer", "ROLE_PROD");
        
        when(authService.login(any(LoginDto.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ROLE_PROD"));
                
        verify(authService, times(1)).login(any(LoginDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserRoleToModAndUpdateUsername() throws Exception {
        RoleUpdateDto roleUpdateDto = new RoleUpdateDto("ROLE_MOD");
        
        when(authService.updateUserRole(1L, "ROLE_MOD")).thenReturn("User role updated successfully.");
        
        mockMvc.perform(put("/api/auth/user/role/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleUpdateDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string("User role updated successfully."));
        
        verify(authService).updateUserRole(1L, "ROLE_MOD");
        
        UsernameUpdateDto usernameUpdateDto = new UsernameUpdateDto("john");
        
        when(authService.updateUserUsername(1L, "john")).thenReturn("User username updated successfully.");
        
        mockMvc.perform(put("/api/auth/user/username/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usernameUpdateDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string("User username updated successfully."));
                
        verify(authService).updateUserUsername(1L, "john");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteProducer() throws Exception {
        doNothing().when(authService).deleteUserById(1L);
        
        mockMvc.perform(delete("/api/auth/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully."));
                
        verify(authService).deleteUserById(1L);
    }
}