package com.projectmanagement.domain.port.output;

import com.projectmanagement.domain.model.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepositoryPort {
    Task save(Task task);

    Optional<Task> findById(UUID id);

    List<Task> findByProjectIdAndDeletedFalse(UUID projectId);
    // Additional methods can be added as requirements evolve
}
