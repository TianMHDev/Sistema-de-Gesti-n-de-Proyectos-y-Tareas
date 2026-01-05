package com.projectmanagement.application.service;

import com.projectmanagement.application.port.input.DeleteTaskUseCase;
import com.projectmanagement.domain.exception.ResourceNotFoundException;
import com.projectmanagement.domain.exception.UnauthorizedAccessException;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.model.Task;
import com.projectmanagement.domain.port.output.CurrentUserPort;
import com.projectmanagement.domain.port.output.ProjectRepositoryPort;
import com.projectmanagement.domain.port.output.TaskRepositoryPort;

import java.util.UUID;

public class DeleteTaskService implements DeleteTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final CurrentUserPort currentUserPort;

    public DeleteTaskService(TaskRepositoryPort taskRepositoryPort, ProjectRepositoryPort projectRepositoryPort,
            CurrentUserPort currentUserPort) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.projectRepositoryPort = projectRepositoryPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public void deleteTask(UUID taskId) {
        UUID currentUserId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedAccessException("User not authenticated"));

        Task task = taskRepositoryPort.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        Project project = projectRepositoryPort.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for task"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("Only the project owner can delete tasks");
        }

        task.setDeleted(true);
        taskRepositoryPort.save(task);
    }
}
