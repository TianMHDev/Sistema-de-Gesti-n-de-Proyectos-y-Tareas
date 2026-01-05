package com.projectmanagement.infrastructure.adapter.persistence;

import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.port.output.ProjectListPort;
import com.projectmanagement.infrastructure.adapter.persistence.mapper.ProjectMapper;
import com.projectmanagement.infrastructure.adapter.persistence.repository.SpringDataProjectRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectListRepositoryAdapter implements ProjectListPort {

    private final SpringDataProjectRepository repository;
    private final ProjectMapper mapper;

    public ProjectListRepositoryAdapter(SpringDataProjectRepository repository, ProjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Project> findAllByOwnerId(java.util.UUID ownerId) {
        return repository.findByOwnerIdAndDeletedFalse(ownerId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
