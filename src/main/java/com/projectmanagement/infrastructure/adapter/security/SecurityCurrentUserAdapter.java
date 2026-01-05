package com.projectmanagement.infrastructure.adapter.security;

import com.projectmanagement.domain.port.output.CurrentUserPort;
import com.projectmanagement.infrastructure.adapter.persistence.entity.UserEntity;
import com.projectmanagement.infrastructure.adapter.persistence.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SecurityCurrentUserAdapter implements CurrentUserPort {

    private final UserRepository userRepository;

    public SecurityCurrentUserAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UUID> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByUsername(username)
                .map(UserEntity::getId);
    }
}
