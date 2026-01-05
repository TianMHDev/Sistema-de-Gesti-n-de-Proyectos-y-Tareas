package com.projectmanagement.infrastructure.adapter.persistence;

import com.projectmanagement.domain.model.User;
import com.projectmanagement.domain.port.output.UserRepositoryPort;
import com.projectmanagement.infrastructure.adapter.persistence.entity.UserEntity;
import com.projectmanagement.infrastructure.adapter.persistence.mapper.UserMapper;
import com.projectmanagement.infrastructure.adapter.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository springDataRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(UserRepository springDataRepository, UserMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        return mapper.toDomain(springDataRepository.save(entity));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        // Simple implementation, ideally optimize with count/exists query
        return springDataRepository.findByUsername(username).isPresent();
    }
}
