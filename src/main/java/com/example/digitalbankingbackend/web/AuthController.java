package com.example.digitalbankingbackend.web;

import com.example.digitalbankingbackend.dtos.auth.*;
import com.example.digitalbankingbackend.services.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.username());
        
        try {
            JwtResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", loginRequest.username(), e.getMessage());
            throw new RuntimeException("Invalid username or password");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        log.info("Registration attempt for user: {}", registerRequest.username());
        
        try {
            MessageResponseDTO response = authService.register(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Registration failed for user {}: {}", registerRequest.username(), e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponseDTO(e.getMessage()));
        }
    }
      @PostMapping("/logout")
    public ResponseEntity<MessageResponseDTO> logoutUser() {
        log.info("User logout request");
        return ResponseEntity.ok(new MessageResponseDTO("User logged out successfully!"));
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        log.info("Refresh token request");
        
        try {
            JwtResponseDTO response = authService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Refresh token failed: {}", e.getMessage());
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
