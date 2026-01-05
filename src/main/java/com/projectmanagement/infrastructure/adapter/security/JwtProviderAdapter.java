package com.projectmanagement.infrastructure.adapter.security;

import com.projectmanagement.domain.port.output.TokenProviderPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtProviderAdapter implements TokenProviderPort {

    private final JwtService jwtService;

    public JwtProviderAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }
}
