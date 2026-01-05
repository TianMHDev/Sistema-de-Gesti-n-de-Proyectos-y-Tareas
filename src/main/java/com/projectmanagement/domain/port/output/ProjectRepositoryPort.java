package com.projectmanagement.domain.port.output;

import com.projectmanagement.domain.model.Project;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepositoryPort {
    Project save(Project project);

    Optional<Project> findById(UUID id);
    // Additional methods as needed
}
