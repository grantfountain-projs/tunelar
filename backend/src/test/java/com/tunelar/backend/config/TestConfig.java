package com.tunelar.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * Test configuration for Spring Boot tests.
 */
@TestConfiguration
public class TestConfig {

    /**
     * Provides a password encoder for tests.
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Provides an authentication manager for tests if one doesn't exist.
     * 
     * @param authenticationConfiguration the authentication configuration
     * @return authentication manager
     * @throws Exception if an error occurs
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}