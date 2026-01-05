package com.projectmanagement.application.service;

import com.projectmanagement.application.port.input.CreateTaskUseCase;
import com.projectmanagement.domain.exception.ResourceNotFoundException;
import com.projectmanagement.domain.exception.UnauthorizedAccessException;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.model.Task;
import com.projectmanagement.domain.port.output.CurrentUserPort;
import com.projectmanagement.domain.port.output.ProjectRepositoryPort;
import com.projectmanagement.domain.port.output.TaskRepositoryPort;

import java.util.UUID;

public class CreateTaskService implements CreateTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final CurrentUserPort currentUserPort;

    public CreateTaskService(TaskRepositoryPort taskRepositoryPort,
            ProjectRepositoryPort projectRepositoryPort,
            CurrentUserPort currentUserPort) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.projectRepositoryPort = projectRepositoryPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public Task createTask(UUID projectId, String title, String description) {
        UUID currentUserId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedAccessException("User not authenticated"));

        Project project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        // Usually only owner can add tasks? Requirement doesn't explicitly strict it
        // for creation,
        // but says "CompleteTask: Solo el propietario puede completar".
        // Assuming implicit owner rule for creation too to be safe/consistent.
        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("Only the project owner can add tasks");
        }

        Task task = new Task(
                UUID.randomUUID(),
                projectId,
                title,
                false,
                false);

        return taskRepositoryPort.save(task);
    }
}
