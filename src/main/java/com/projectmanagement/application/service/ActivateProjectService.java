package com.projectmanagement.application.service;

import com.projectmanagement.application.port.input.ActivateProjectUseCase;
import com.projectmanagement.domain.exception.BusinessRuleViolationException;
import com.projectmanagement.domain.exception.ResourceNotFoundException;
import com.projectmanagement.domain.exception.UnauthorizedAccessException;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.model.ProjectStatus;
import com.projectmanagement.domain.model.Task;
import com.projectmanagement.domain.port.output.*;

import java.util.List;
import java.util.UUID;

public class ActivateProjectService implements ActivateProjectUseCase {

    private final ProjectRepositoryPort projectRepository;
    private final TaskRepositoryPort taskRepository;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;
    private final CurrentUserPort currentUserPort;

    public ActivateProjectService(ProjectRepositoryPort projectRepository,
            TaskRepositoryPort taskRepository,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort,
            CurrentUserPort currentUserPort) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public void activateProject(UUID projectId) {
        UUID currentUserId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedAccessException("User not authenticated"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        // Business Rule: Check if the current user is the owner of the project
        // Only the owner can modify (in this case, activate) the project.
        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("Only the project owner can activate the project");
        }

        List<Task> tasks = taskRepository.findByProjectIdAndDeletedFalse(projectId);

        // Business Rule: A project can only be activated if it has at least one active
        // task.
        // We consider "active task" as any task that is not deleted.
        // If the project has no tasks, we cannot activate it.

        if (tasks.isEmpty()) {
            throw new BusinessRuleViolationException("Project must have at least one task to be activated");
        }

        project.setStatus(ProjectStatus.ACTIVE);
        projectRepository.save(project);

        auditLogPort.register("ACTIVATE_PROJECT", projectId);
        notificationPort.notify("Project activated: " + project.getName());
    }
}
