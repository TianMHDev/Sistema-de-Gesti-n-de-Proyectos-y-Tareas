package com.projectmanagement.infrastructure.adapter.persistence;

import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.port.output.ProjectRepositoryPort;
import com.projectmanagement.infrastructure.adapter.persistence.entity.ProjectEntity;
import com.projectmanagement.infrastructure.adapter.persistence.mapper.ProjectMapper;
import com.projectmanagement.infrastructure.adapter.persistence.repository.SpringDataProjectRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {

    private final SpringDataProjectRepository repository;
    private final ProjectMapper mapper;

    public ProjectRepositoryAdapter(SpringDataProjectRepository repository, ProjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @org.springframework.cache.annotation.CacheEvict(value = "projects", key = "#project.ownerId")
    public Project save(Project project) {
        ProjectEntity entity = mapper.toEntity(project);
        ProjectEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return repository.findById(id)
                .filter(entity -> !entity.isDeleted())
                .map(mapper::toDomain);
    }
}
