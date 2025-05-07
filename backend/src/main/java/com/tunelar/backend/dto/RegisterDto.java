package com.tunelar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Information needed to register a new customer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    /**
     * name to register
     */
    private String name;
    /**
     * username to register
     */
    private String username;
    /**
     * email to register
     */
    private String email;
    /**
     * password to register
     */
    private String password;
}