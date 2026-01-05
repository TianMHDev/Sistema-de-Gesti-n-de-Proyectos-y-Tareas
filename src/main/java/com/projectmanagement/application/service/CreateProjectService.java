package com.projectmanagement.application.service;

import com.projectmanagement.application.port.input.CreateProjectUseCase;
import com.projectmanagement.domain.exception.UnauthorizedAccessException;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.model.ProjectStatus;
import com.projectmanagement.domain.port.output.CurrentUserPort;
import com.projectmanagement.domain.port.output.ProjectRepositoryPort;

import java.util.UUID;

public class CreateProjectService implements CreateProjectUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;
    private final CurrentUserPort currentUserPort;

    public CreateProjectService(ProjectRepositoryPort projectRepositoryPort, CurrentUserPort currentUserPort) {
        this.projectRepositoryPort = projectRepositoryPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public Project createProject(String name, String description) {
        UUID currentUserId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedAccessException("User not authenticated"));

        // Description might be added to model if requested, but requirement only
        // mentioned name for Project Model
        // "Project: id (UUID), ownerId (UUID), name, status (DRAFT, ACTIVE), deleted
        // (boolean)."
        // User request didn't strictly say description is in model, but REST API
        // usually sends it.
        // I will stick to model definition: Project(id, ownerId, name, status, deleted)

        Project project = new Project(
                UUID.randomUUID(),
                currentUserId,
                name,
                ProjectStatus.DRAFT,
                false);

        return projectRepositoryPort.save(project);
    }
}
