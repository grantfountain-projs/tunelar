package com.tunelar.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import com.tunelar.backend.config.TestSecurityConfig;
import com.tunelar.backend.service.AuthService;

/**
 * Simple test to ensure the context loads successfully.
 */
@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @MockBean
    private AuthService authService;
    
    /**
     * Simple smoke test to ensure the configuration is valid.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void contextLoads() {
        // This empty test just validates that the context loads successfully
    }
}