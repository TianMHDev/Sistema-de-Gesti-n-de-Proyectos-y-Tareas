package com.projectmanagement.infrastructure.rest.controller;

import com.projectmanagement.application.dto.AuthResponse;
import com.projectmanagement.application.dto.LoginRequest;
import com.projectmanagement.application.dto.RegisterRequest;
import com.projectmanagement.application.port.input.AuthUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Register user", description = "Registers a new user and returns a JWT.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authUseCase.register(request));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Login user", description = "Authenticates a user and returns a JWT.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authUseCase.login(request));
    }
}
