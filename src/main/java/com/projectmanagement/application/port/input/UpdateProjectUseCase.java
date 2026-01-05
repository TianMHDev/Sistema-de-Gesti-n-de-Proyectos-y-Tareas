package com.projectmanagement.application.port.input;

import com.projectmanagement.domain.model.Project;
import java.util.UUID;

public interface UpdateProjectUseCase {
    Project updateProject(UUID projectId, String name);
}
