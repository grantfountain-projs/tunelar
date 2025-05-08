package com.tunelar.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        UserDetails adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();
        
        UserDetails modUser = User.builder()
                .username("mod")
                .password(passwordEncoder().encode("password"))
                .roles("MOD")
                .build();
        
        UserDetails prodUser = User.builder()
                .username("prod")
                .password(passwordEncoder().encode("password"))
                .roles("PROD")
                .build();
        
        return new InMemoryUserDetailsManager(adminUser, modUser, prodUser);
    }

    @Bean
    @Primary
    @Order(1) // Higher priority than the main security config
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        // Add a specific matcher to avoid conflict
        http.securityMatcher("/**")
            .csrf().disable()
            .authorizeRequests()
                .anyRequest().permitAll();
        
        return http.build();
    }
}