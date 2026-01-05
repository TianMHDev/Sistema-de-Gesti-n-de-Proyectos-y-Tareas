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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateProjectServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepository;
    @Mock
    private TaskRepositoryPort taskRepository;
    @Mock
    private AuditLogPort auditLogPort;
    @Mock
    private NotificationPort notificationPort;
    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private ActivateProjectService activateProjectService;

    private UUID userId;
    private UUID projectId;
    private Project project;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        project = new Project(projectId, userId, "Test Project", ProjectStatus.DRAFT, false);
    }

    @Test
    void confirmActivateProject_WhenValid_ShouldActivateAndNotify() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(Optional.of(userId));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectIdAndDeletedFalse(projectId))
                .thenReturn(List.of(new Task(UUID.randomUUID(), projectId, "Task 1", false, false)));

        // Act
        activateProjectService.activateProject(projectId);

        // Assert
        assertEquals(ProjectStatus.ACTIVE, project.getStatus());
        verify(projectRepository).save(project);
        verify(auditLogPort).register(eq("ACTIVATE_PROJECT"), eq(projectId));
        verify(notificationPort).notify(anyString());
    }

    @Test
    void activateProject_WhenUserNotAuthenticated_ShouldThrowUnauthorized() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> activateProjectService.activateProject(projectId));
        verifyNoInteractions(projectRepository);
    }

    @Test
    void activateProject_WhenProjectNotFound_ShouldThrowResourceNotFound() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(Optional.of(userId));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> activateProjectService.activateProject(projectId));
    }

    @Test
    void activateProject_WhenUserNotOwner_ShouldThrowUnauthorized() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        when(currentUserPort.getCurrentUserId()).thenReturn(Optional.of(otherUserId));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> activateProjectService.activateProject(projectId));
    }

    @Test
    void activateProject_WhenNoTasks_ShouldThrowBusinessRuleViolation() {
        // Arrange
        when(currentUserPort.getCurrentUserId()).thenReturn(Optional.of(userId));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectIdAndDeletedFalse(projectId)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(BusinessRuleViolationException.class, () -> activateProjectService.activateProject(projectId));
    }
}
