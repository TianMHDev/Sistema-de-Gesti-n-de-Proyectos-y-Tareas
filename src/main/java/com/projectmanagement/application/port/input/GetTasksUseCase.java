package com.projectmanagement.application.port.input;

import com.projectmanagement.domain.model.Task;
import java.util.List;
import java.util.UUID;

public interface GetTasksUseCase {
    List<Task> getTasksByProjectId(UUID projectId);
}
