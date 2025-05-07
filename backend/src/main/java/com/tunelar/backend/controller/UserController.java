package com.tunelar.backend.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tunelar.backend.dto.UserDto;
import com.tunelar.backend.model.User;
import com.tunelar.backend.service.AuthService;

/**
 * Controller for user functionality.
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Gets a user by ID
     *
     * @param id ID of the user
     * @return the user information
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
    @GetMapping("/byId/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") final Long id) {
        User user = authService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
    @GetMapping("/byUsername/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") final String username) {
        User user = authService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Gets all users
     *
     * @return list of all users
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = authService.getAllUsers();
        
        return ResponseEntity.ok(users);
    }
    
    /**
     * Converts a User entity to a UserDto
     *
     * @param user the user entity
     * @return the user DTO
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        
        // Convert roles to set of role names
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
        dto.setRoles(roleNames);
        
        return dto;
    }
}