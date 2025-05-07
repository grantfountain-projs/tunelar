package com.tunelar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response for authenticated and authorized user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {
    /**
     * the access token
     */
    private String accessToken;
    /**
     * token type, always Bearer
     */
    private String tokenType = "Bearer";
    /**
     * the role of the user
     */
    private String role;
}