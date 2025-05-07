package com.tunelar.backend.controller;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tunelar.backend.dto.JwtAuthResponse;
import com.tunelar.backend.dto.LoginDto;
import com.tunelar.backend.dto.RegisterDto;
import com.tunelar.backend.dto.RoleUpdateDto;
import com.tunelar.backend.dto.UsernameUpdateDto;
import com.tunelar.backend.service.AuthService;

import lombok.AllArgsConstructor;

/**
 * Controller for authentication and user management functionality in the Tunelar application.
 * Provides endpoints for user registration, login, deletion, and profile updates.
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    
    /**
     * Service handling authentication and user operations
     */
    private final AuthService authService;
    
    
    /**
     * Registers a new user with the system.
     * Creates a standard user account with default permissions.
     *
     * @param registerDto object containing registration information (username, email, password)
     * @return response indicating success or failure of the registration
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody final RegisterDto registerDto) {
        final String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginDto object containing login credentials (username and password)
     * @return JWT token and user details upon successful authentication
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody final LoginDto loginDto) {
        final JwtAuthResponse jwtAuthResponse = authService.login(loginDto);
        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }
    
    /**
     * Deletes a user account from the system.
     * Requires ADMIN role.
     *
     * @param id ID of the user to delete
     * @return response indicating successful deletion
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") final Long id) {
        authService.deleteUserById(id);
        // Log the deletion action
        /**logService.logUserActionWithAuthentication(SecurityContextHolder.getContext().getAuthentication(), 
                "User", "Delete");**/
        return ResponseEntity.ok("User deleted successfully.");
    }
    
    /**
     * Updates a user's role in the system.
     * Requires ADMIN role.
     *
     * @param id ID of the user to update
     * @param roleUpdateDto object containing the new role information
     * @return response indicating the success of the operation
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/role/{id}")
    public ResponseEntity<String> updateUserRole(@PathVariable("id") final Long id,
            @RequestBody final RoleUpdateDto roleUpdateDto) {
        final String response = authService.updateUserRole(id, roleUpdateDto.getRole());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Updates a user's username.
     * Requires ADMIN role.
     *
     * @param id ID of the user to update
     * @param usernameUpdateDto object containing the new username
     * @return response indicating the success of the operation
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/username/{id}")
    public ResponseEntity<String> updateUserUsername(@PathVariable("id") final Long id,
            @RequestBody final UsernameUpdateDto usernameUpdateDto) {
        final String response = authService.updateUserUsername(id, usernameUpdateDto.getUsername());
        return ResponseEntity.ok(response);
    }
}