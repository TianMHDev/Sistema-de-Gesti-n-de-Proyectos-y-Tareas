package com.projectmanagement.infrastructure.adapter.persistence.mapper;

import com.projectmanagement.domain.model.User;
import com.projectmanagement.infrastructure.adapter.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toDomain(UserEntity entity) {
        if (entity == null)
            return null;
        return new User(entity.getId(), entity.getUsername(), entity.getEmail(), entity.getPassword());
    }

    public UserEntity toEntity(User domain) {
        if (domain == null)
            return null;
        return new UserEntity(domain.getId(), domain.getUsername(), domain.getEmail(), domain.getPassword());
    }
}
