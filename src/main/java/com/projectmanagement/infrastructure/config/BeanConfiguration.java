package com.projectmanagement.infrastructure.config;

import com.projectmanagement.application.port.input.*;
import com.projectmanagement.application.service.*;
import com.projectmanagement.domain.port.output.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ActivateProjectUseCase activateProjectUseCase(
            ProjectRepositoryPort projectRepository,
            TaskRepositoryPort taskRepository,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort,
            CurrentUserPort currentUserPort) {
        return new ActivateProjectService(projectRepository, taskRepository, auditLogPort, notificationPort,
                currentUserPort);
    }

    @Bean
    public CompleteTaskUseCase completeTaskUseCase(
            TaskRepositoryPort taskRepository,
            ProjectRepositoryPort projectRepository,
            AuditLogPort auditLogPort,
            NotificationPort notificationPort,
            CurrentUserPort currentUserPort) {
        return new CompleteTaskService(taskRepository, projectRepository, auditLogPort, notificationPort,
                currentUserPort);
    }

    @Bean
    public AuthUseCase authUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            TokenProviderPort tokenProviderPort,
            org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
        return new AuthService(userRepositoryPort, passwordEncoderPort, tokenProviderPort, userDetailsService);
    }

    @Bean
    public CreateProjectUseCase createProjectUseCase(
            ProjectRepositoryPort projectRepositoryPort,
            CurrentUserPort currentUserPort) {
        return new CreateProjectService(projectRepositoryPort, currentUserPort);
    }

    @Bean
    public CreateTaskUseCase createTaskUseCase(
            TaskRepositoryPort taskRepositoryPort,
            ProjectRepositoryPort projectRepositoryPort,
            CurrentUserPort currentUserPort) {
        return new CreateTaskService(taskRepositoryPort, projectRepositoryPort, currentUserPort);
    }

    @Bean
    public GetProjectsUseCase getProjectsUseCase(ProjectListPort projectListPort, CurrentUserPort currentUserPort) {
        return new GetProjectsService(projectListPort, currentUserPort);
    }

    @Bean
    public GetTasksUseCase getTasksUseCase(TaskRepositoryPort taskRepositoryPort,
            ProjectRepositoryPort projectRepositoryPort,
            CurrentUserPort currentUserPort) {
        return new GetTasksService(taskRepositoryPort, projectRepositoryPort, currentUserPort);
    }

    @Bean
    public UpdateProjectUseCase updateProjectUseCase(ProjectRepositoryPort projectRepositoryPort,
            CurrentUserPort currentUserPort) {
        return new UpdateProjectService(projectRepositoryPort, currentUserPort);
    }

    @Bean
    public DeleteProjectUseCase deleteProjectUseCase(ProjectRepositoryPort projectRepositoryPort,
            CurrentUserPort currentUserPort) {
        return new DeleteProjectService(projectRepositoryPort, currentUserPort);
    }

    @Bean
    public UpdateTaskUseCase updateTaskUseCase(TaskRepositoryPort taskRepositoryPort,
            ProjectRepositoryPort projectRepositoryPort, CurrentUserPort currentUserPort) {
        return new UpdateTaskService(taskRepositoryPort, projectRepositoryPort, currentUserPort);
    }

    @Bean
    public DeleteTaskUseCase deleteTaskUseCase(TaskRepositoryPort taskRepositoryPort,
            ProjectRepositoryPort projectRepositoryPort, CurrentUserPort currentUserPort) {
        return new DeleteTaskService(taskRepositoryPort, projectRepositoryPort, currentUserPort);
    }
}
