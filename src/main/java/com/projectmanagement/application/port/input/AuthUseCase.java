package com.projectmanagement.application.port.input;

import com.projectmanagement.application.dto.AuthResponse;
import com.projectmanagement.application.dto.LoginRequest;
import com.projectmanagement.application.dto.RegisterRequest;

public interface AuthUseCase {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
