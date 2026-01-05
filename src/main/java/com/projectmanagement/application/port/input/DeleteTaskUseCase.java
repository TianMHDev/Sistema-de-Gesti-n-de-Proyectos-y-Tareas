package com.projectmanagement.application.port.input;

import java.util.UUID;

public interface DeleteTaskUseCase {
    void deleteTask(UUID taskId);
}
