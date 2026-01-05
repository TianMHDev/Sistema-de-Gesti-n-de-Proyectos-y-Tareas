package com.projectmanagement.application.service;

import com.projectmanagement.application.port.input.CompleteTaskUseCase;
import com.projectmanagement.domain.exception.BusinessRuleViolationException;
import com.projectmanagement.domain.exception.ResourceNotFoundException;
import com.projectmanagement.domain.exception.UnauthorizedAccessException;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.model.Task;
import com.projectmanagement.domain.port.output.*;

import java.util.UUID;

public class CompleteTaskService implements CompleteTaskUseCase {

    private final TaskRepositoryPort taskRepository;
    private final ProjectRepositoryPort projectRepository;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;
    private final CurrentUserPort currentUserPort;

    public CompleteTaskService(TaskRepositoryPort taskRepository,
            ProjectRepositoryPort projectRepository,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort,
            CurrentUserPort currentUserPort) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public void completeTask(UUID taskId) {
        UUID currentUserId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedAccessException("User not authenticated"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        // Business Rule: A completed task cannot be modified.
        if (task.isCompleted()) {
            throw new BusinessRuleViolationException("Task is already completed and cannot be modified");
        }

        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for task: " + taskId));

        // Business Rule: Only the owner of the project can complete its tasks.
        // We verify that the current authenticated user matches the project owner.
        if (!project.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("Only the project owner can complete tasks");
        }

        task.setCompleted(true);
        taskRepository.save(task);

        // Audit and Notification as required by the business rules
        auditLogPort.register("COMPLETE_TASK", taskId);
        notificationPort.notify("Task completed: " + task.getTitle());
    }
}
