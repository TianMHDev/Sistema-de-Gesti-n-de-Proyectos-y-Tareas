package com.projectmanagement.domain.port.output;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenProviderPort {
    String generateToken(UserDetails userDetails);
}
