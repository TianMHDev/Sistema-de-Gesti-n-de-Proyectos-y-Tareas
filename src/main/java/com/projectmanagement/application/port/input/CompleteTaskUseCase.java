package com.projectmanagement.application.port.input;

import java.util.UUID;

public interface CompleteTaskUseCase {
    void completeTask(UUID taskId);
}
