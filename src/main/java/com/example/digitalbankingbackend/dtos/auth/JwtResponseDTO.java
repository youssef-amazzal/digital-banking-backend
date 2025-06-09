package com.example.digitalbankingbackend.dtos.auth;

import com.example.digitalbankingbackend.enums.Role;

public record JwtResponseDTO(
        String token,
        String type,
        String username,
        String email,
        Role role,
        String firstName,
        String lastName,
        String refreshToken
) {
    public JwtResponseDTO(String token, String username, String email, Role role, String firstName, String lastName) {
        this(token, "Bearer", username, email, role, firstName, lastName, null);
    }
    
    public JwtResponseDTO(String token, String username, String email, Role role, String firstName, String lastName, String refreshToken) {
        this(token, "Bearer", username, email, role, firstName, lastName, refreshToken);
    }
}
