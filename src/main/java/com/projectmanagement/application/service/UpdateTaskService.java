package com.projectmanagement.application.service;

import com.projectmanagement.application.port.input.UpdateTaskUseCase;
import com.projectmanagement.domain.exception.BusinessRuleViolationException;
import com.projectmanagement.domain.exception.ResourceNotFoundException;
import com.projectmanagement.domain.exception.UnauthorizedAccessException;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.model.Task;
import com.projectmanagement.domain.port.output.CurrentUserPort;
import com.projectmanagement.domain.port.output.ProjectRepositoryPort;
import com.projectmanagement.domain.port.output.TaskRepositoryPort;

import java.util.UUID;

public class UpdateTaskService implements UpdateTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final CurrentUserPort currentUserPort;

    public UpdateTaskService(TaskRepositoryPort taskRepositoryPort, ProjectRepositoryPort projectRepositoryPort,
            CurrentUserPort currentUserPort) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.projectRepositoryPort = projectRepositoryPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public Task updateTask(UUID taskId, String title) {
        UUID currentUserId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedAccessException("User not authenticated"));

        Task task = taskRepositoryPort.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        if (task.isCompleted()) {
            throw new BusinessRuleViolationException("Completed tasks cannot be modified");
        }

        Project project = projectRepositoryPort.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for task"));

        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("Only the project owner can update tasks");
        }

        task.setTitle(title);
        return taskRepositoryPort.save(task);
    }
}
