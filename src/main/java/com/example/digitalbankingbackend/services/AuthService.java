package com.example.digitalbankingbackend.services;

import com.example.digitalbankingbackend.dtos.auth.*;
import com.example.digitalbankingbackend.entities.RefreshToken;
import com.example.digitalbankingbackend.entities.User;
import com.example.digitalbankingbackend.enums.Role;
import com.example.digitalbankingbackend.repositories.UserRepository;
import com.example.digitalbankingbackend.security.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
      public JwtResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.username(), 
                loginRequest.password()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        log.info("User {} successfully logged in", loginRequest.username());
        
        return new JwtResponseDTO(
            jwt,
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getFirstName(),
            user.getLastName(),
            refreshToken.getToken()
        );
    }
    
    public MessageResponseDTO register(RegisterRequestDTO registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        
        // Create new user account
        User user = new User();
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setFirstName(registerRequest.firstName());
        user.setLastName(registerRequest.lastName());
        
        // Set default role as USER
        user.setRole(Role.USER);
        
        userRepository.save(user);
        
        log.info("User {} successfully registered", registerRequest.username());
        
        return new MessageResponseDTO("User registered successfully!");
    }
      public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public JwtResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String requestRefreshToken = request.refreshToken();
        
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return new JwtResponseDTO(
                            token,
                            user.getUsername(),
                            user.getEmail(),
                            user.getRole(),
                            user.getFirstName(),
                            user.getLastName(),
                            requestRefreshToken
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
}
