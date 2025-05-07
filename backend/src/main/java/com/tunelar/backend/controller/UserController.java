package com.tunelar.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tunelar.backend.model.User;
import com.tunelar.backend.service.AuthService;

import lombok.AllArgsConstructor;

/**
 * Controller for user-related operations in the Tunelar application.
 * Provides endpoints for retrieving user information.
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    
    /**
     * Service handling authentication and user operations
     */
    private final AuthService authService;
    
    /**
     * Retrieves a user by their ID.
     * Requires ADMIN role.
     *
     * @param id the ID of the user to retrieve
     * @return a ResponseEntity containing the user or appropriate error response
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/byId/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") final Long id) {
        final User user = authService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Retrieves a user by their username.
     * Requires ADMIN role.
     *
     * @param username the username of the user to retrieve
     * @return a ResponseEntity containing the user or appropriate error response
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/byUsername/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") final String username) {
        final User user = authService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Retrieves a user's ID by their username.
     * Accessible to ADMIN, STAFF, and CUSTOMER roles.
     *
     * @param username the username of the user to get the ID for
     * @return a ResponseEntity containing the user ID or appropriate error response
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MOD', 'PROD')")
    @GetMapping("/id/{username}")
    public ResponseEntity<Long> getUserIDByUsername(@PathVariable("username") final String username) {
        final User user = authService.getUserByUsername(username);
        return ResponseEntity.ok(user.getId());
    }
    
    /**
     * Retrieves all users in the system.
     * Accessible to ADMIN and STAFF roles.
     *
     * @return a ResponseEntity containing a list of all users or appropriate error response
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        final List<User> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}