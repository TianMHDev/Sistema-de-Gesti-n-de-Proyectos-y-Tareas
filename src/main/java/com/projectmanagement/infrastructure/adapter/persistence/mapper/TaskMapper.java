package com.projectmanagement.infrastructure.adapter.persistence.mapper;

import com.projectmanagement.domain.model.Task;
import com.projectmanagement.infrastructure.adapter.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toDomain(TaskEntity entity) {
        if (entity == null)
            return null;
        return new Task(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.isCompleted(),
                entity.isDeleted());
    }

    public TaskEntity toEntity(Task domain) {
        if (domain == null)
            return null;
        TaskEntity entity = new TaskEntity();
        entity.setId(domain.getId());
        entity.setProjectId(domain.getProjectId());
        entity.setTitle(domain.getTitle());
        entity.setCompleted(domain.isCompleted());
        entity.setDeleted(domain.isDeleted());
        return entity;
    }
}
