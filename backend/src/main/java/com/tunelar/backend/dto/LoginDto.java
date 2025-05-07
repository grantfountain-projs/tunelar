package com.tunelar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Information to login a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    /**
     * the username of email of the user
     */
    private String usernameOrEmail;
    /**
     * the password of the user
     */
    private String password;

}