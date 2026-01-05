package com.projectmanagement.infrastructure.adapter.persistence;

import com.projectmanagement.domain.model.Task;
import com.projectmanagement.domain.port.output.TaskRepositoryPort;
import com.projectmanagement.infrastructure.adapter.persistence.entity.TaskEntity;
import com.projectmanagement.infrastructure.adapter.persistence.mapper.TaskMapper;
import com.projectmanagement.infrastructure.adapter.persistence.repository.SpringDataTaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TaskRepositoryAdapter implements TaskRepositoryPort {

    private final SpringDataTaskRepository repository;
    private final TaskMapper mapper;

    public TaskRepositoryAdapter(SpringDataTaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = mapper.toEntity(task);
        TaskEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return repository.findById(id)
                .filter(entity -> !entity.isDeleted())
                .map(mapper::toDomain);
    }

    @Override
    public List<Task> findByProjectIdAndDeletedFalse(UUID projectId) {
        return repository.findByProjectIdAndDeletedFalse(projectId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
