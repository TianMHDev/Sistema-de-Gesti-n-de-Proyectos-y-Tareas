package com.projectmanagement.application.port.input;

import java.util.UUID;

public interface DeleteProjectUseCase {
    void deleteProject(UUID projectId);
}
