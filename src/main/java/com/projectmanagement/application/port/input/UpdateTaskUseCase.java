package com.projectmanagement.application.port.input;

import com.projectmanagement.domain.model.Task;
import java.util.UUID;

public interface UpdateTaskUseCase {
    Task updateTask(UUID taskId, String title);
}
