package com.projectmanagement.application.port.input;

import com.projectmanagement.domain.model.Task;
import java.util.UUID;

public interface CreateTaskUseCase {
    Task createTask(UUID projectId, String title, String description);
}
