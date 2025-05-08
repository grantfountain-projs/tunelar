package com.tunelar.backend.config;

import com.tunelar.backend.repository.RoleRepository;
import com.tunelar.backend.repository.UserRepository;
import com.tunelar.backend.security.JwtAuthenticationEntryPoint;
import com.tunelar.backend.security.JwtAuthenticationFilter;
import com.tunelar.backend.security.JwtTokenProvider;
import com.tunelar.backend.service.AuthService;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@Profile("test")
public class MockMvcTestConfig {

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private RoleRepository roleRepository;
    
    @MockBean
    private AuthService authService;

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
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    @Primary
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        // Disable CSRF and frame options for testing
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers(mvc.pattern("/api/auth/**")).permitAll()
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        
        return http.build();
    }
    
    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
        return authentication -> authentication; // This is a no-op authentication manager for testing
    }
}