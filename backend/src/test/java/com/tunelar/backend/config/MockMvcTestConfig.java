package com.tunelar.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.tunelar.backend.security.JwtTokenProvider;
import com.tunelar.backend.security.JwtAuthenticationEntryPoint;
import com.tunelar.backend.security.JwtAuthenticationFilter;

@TestConfiguration
public class MockMvcTestConfig {

    @MockBean
    public JwtTokenProvider jwtTokenProvider;
    
    @MockBean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @MockBean
    public JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    @Primary
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/api/auth/**").permitAll();
                auth.requestMatchers("/api/user/**").hasAnyRole("ADMIN", "MOD");
                auth.anyRequest().authenticated();
            });
        
        return http.build();
    }
}