package com.projectmanagement.application.service;

import com.projectmanagement.application.port.input.UpdateProjectUseCase;
import com.projectmanagement.domain.exception.ResourceNotFoundException;
import com.projectmanagement.domain.exception.UnauthorizedAccessException;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.port.output.CurrentUserPort;
import com.projectmanagement.domain.port.output.ProjectRepositoryPort;

import java.util.UUID;

public class UpdateProjectService implements UpdateProjectUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;
    private final CurrentUserPort currentUserPort;

    public UpdateProjectService(ProjectRepositoryPort projectRepositoryPort, CurrentUserPort currentUserPort) {
        this.projectRepositoryPort = projectRepositoryPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public Project updateProject(UUID projectId, String name) {
        UUID currentUserId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedAccessException("User not authenticated"));

        Project project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("Only the owner can update the project");
        }

        project.setName(name);
        return projectRepositoryPort.save(project);
    }
}
