package com.projectmanagement.application.port.input;

import java.util.UUID;

public interface ActivateProjectUseCase {
    void activateProject(UUID projectId);
}
