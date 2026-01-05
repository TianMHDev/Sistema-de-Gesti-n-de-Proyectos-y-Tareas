package com.projectmanagement.application.service;

import com.projectmanagement.application.port.input.GetTasksUseCase;
import com.projectmanagement.domain.model.Task;
import com.projectmanagement.domain.port.output.TaskRepositoryPort;

import java.util.List;
import java.util.UUID;

public class GetTasksService implements GetTasksUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final com.projectmanagement.domain.port.output.ProjectRepositoryPort projectRepositoryPort;
    private final com.projectmanagement.domain.port.output.CurrentUserPort currentUserPort;

    public GetTasksService(TaskRepositoryPort taskRepositoryPort,
            com.projectmanagement.domain.port.output.ProjectRepositoryPort projectRepositoryPort,
            com.projectmanagement.domain.port.output.CurrentUserPort currentUserPort) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.projectRepositoryPort = projectRepositoryPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public List<Task> getTasksByProjectId(UUID projectId) {
        UUID currentUserId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new com.projectmanagement.domain.exception.UnauthorizedAccessException(
                        "User not authenticated"));

        com.projectmanagement.domain.model.Project project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new com.projectmanagement.domain.exception.ResourceNotFoundException(
                        "Project not found: " + projectId));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new com.projectmanagement.domain.exception.UnauthorizedAccessException(
                    "Denied access to project tasks");
        }

        return taskRepositoryPort.findByProjectIdAndDeletedFalse(projectId);
    }
}
