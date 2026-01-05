package com.projectmanagement.application.port.input;

import com.projectmanagement.domain.model.Project;
import java.util.UUID;

public interface CreateProjectUseCase {
    Project createProject(String name, String description);
}
