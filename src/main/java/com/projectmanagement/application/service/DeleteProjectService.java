package com.projectmanagement.application.service;

import com.projectmanagement.application.port.input.DeleteProjectUseCase;
import com.projectmanagement.domain.exception.ResourceNotFoundException;
import com.projectmanagement.domain.exception.UnauthorizedAccessException;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.port.output.CurrentUserPort;
import com.projectmanagement.domain.port.output.ProjectRepositoryPort;

import java.util.UUID;

public class DeleteProjectService implements DeleteProjectUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;
    private final CurrentUserPort currentUserPort;

    public DeleteProjectService(ProjectRepositoryPort projectRepositoryPort, CurrentUserPort currentUserPort) {
        this.projectRepositoryPort = projectRepositoryPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public void deleteProject(UUID projectId) {
        UUID currentUserId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedAccessException("User not authenticated"));

        Project project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("Only the owner can delete the project");
        }

        project.setDeleted(true);
        projectRepositoryPort.save(project);
    }
}
