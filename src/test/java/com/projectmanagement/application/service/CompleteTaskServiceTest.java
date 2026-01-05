package com.projectmanagement.application.service;

import com.projectmanagement.domain.exception.BusinessRuleViolationException;
import com.projectmanagement.domain.exception.ResourceNotFoundException;
import com.projectmanagement.domain.exception.UnauthorizedAccessException;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.model.ProjectStatus;
import com.projectmanagement.domain.model.Task;
import com.projectmanagement.domain.port.output.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteTaskServiceTest {

    @Mock
    private TaskRepositoryPort taskRepository;
    @Mock
    private ProjectRepositoryPort projectRepository;
    @Mock
    private AuditLogPort auditLogPort;
    @Mock
    private NotificationPort notificationPort;
    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private CompleteTaskService completeTaskService;

    private UUID userId;
    private UUID projectId;
    private UUID taskId;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        project = new Project(projectId, userId, "Test Project", ProjectStatus.ACTIVE, false);
        task = new Task(taskId, projectId, "Test Task", false, false);
    }

    @Test
    void completeTask_WhenValid_ShouldCompleteAndNotify() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(Optional.of(userId));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        completeTaskService.completeTask(taskId);

        // Assert
        assertTrue(task.isCompleted());
        verify(taskRepository).save(task);
        verify(auditLogPort).register(eq("COMPLETE_TASK"), eq(taskId));
        verify(notificationPort).notify(anyString());
    }

    @Test
    void completeTask_WhenTaskAlreadyCompleted_ShouldThrowBusinessRuleViolation() {
        // Arrange
        task.setCompleted(true);
        when(currentUserPort.getCurrentUserId()).thenReturn(Optional.of(userId));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act & Assert
        assertThrows(BusinessRuleViolationException.class, () -> completeTaskService.completeTask(taskId));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void completeTask_WhenUserNotOwner_ShouldThrowUnauthorized() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        when(currentUserPort.getCurrentUserId()).thenReturn(Optional.of(otherUserId));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> completeTaskService.completeTask(taskId));
    }

    @Test
    void completeTask_WhenTaskNotFound_ShouldThrowResourceNotFound() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(Optional.of(userId));
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> completeTaskService.completeTask(taskId));
    }
}
