package com.projectmanagement.infrastructure.adapter.persistence.mapper;

import com.projectmanagement.domain.model.Project;
import com.projectmanagement.infrastructure.adapter.persistence.entity.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public Project toDomain(ProjectEntity entity) {
        if (entity == null)
            return null;
        return new Project(
                entity.getId(),
                entity.getOwnerId(),
                entity.getName(),
                entity.getStatus(),
                entity.isDeleted());
    }

    public ProjectEntity toEntity(Project domain) {
        if (domain == null)
            return null;
        ProjectEntity entity = new ProjectEntity();
        entity.setId(domain.getId());
        entity.setOwnerId(domain.getOwnerId());
        entity.setName(domain.getName());
        entity.setStatus(domain.getStatus());
        entity.setDeleted(domain.isDeleted());
        return entity;
    }
}
