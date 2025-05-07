package com.tunelar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Information needed to update a user's role.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsernameUpdateDto {
    /**
     * the username for updating
     */
    private String username;
}