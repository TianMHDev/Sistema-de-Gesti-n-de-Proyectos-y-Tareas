package com.projectmanagement.application.service;

import com.projectmanagement.application.dto.AuthResponse;
import com.projectmanagement.application.dto.LoginRequest;
import com.projectmanagement.application.dto.RegisterRequest;
import com.projectmanagement.application.port.input.AuthUseCase;
import com.projectmanagement.domain.exception.BusinessRuleViolationException;
import com.projectmanagement.domain.exception.UnauthorizedAccessException;
import com.projectmanagement.domain.model.User;
import com.projectmanagement.domain.port.output.PasswordEncoderPort;
import com.projectmanagement.domain.port.output.TokenProviderPort;
import com.projectmanagement.domain.port.output.UserRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;
    private final UserDetailsService userDetailsService; // Spring Security interface needed for TokenProvider

    public AuthService(UserRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            TokenProviderPort tokenProviderPort,
            UserDetailsService userDetailsService) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepositoryPort.existsByUsername(request.getUsername())) {
            throw new BusinessRuleViolationException("Username already exists");
        }

        User user = new User(
                UUID.randomUUID(),
                request.getUsername(),
                request.getEmail(),
                passwordEncoderPort.encode(request.getPassword()));

        userRepositoryPort.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = tokenProviderPort.generateToken(userDetails);

        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepositoryPort.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid credentials"));

        if (!passwordEncoderPort.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = tokenProviderPort.generateToken(userDetails);

        return new AuthResponse(token);
    }
}
