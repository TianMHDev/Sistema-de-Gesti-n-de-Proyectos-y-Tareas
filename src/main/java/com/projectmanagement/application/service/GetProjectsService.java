package com.projectmanagement.application.service;

import com.projectmanagement.application.port.input.GetProjectsUseCase;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.port.output.ProjectListPort;

import java.util.List;

public class GetProjectsService implements GetProjectsUseCase {

    private final ProjectListPort projectListPort;
    private final com.projectmanagement.domain.port.output.CurrentUserPort currentUserPort;

    public GetProjectsService(ProjectListPort projectListPort,
            com.projectmanagement.domain.port.output.CurrentUserPort currentUserPort) {
        this.projectListPort = projectListPort;
        this.currentUserPort = currentUserPort;
    }

    @Override
    public List<Project> getProjects() {
        java.util.UUID userId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new com.projectmanagement.domain.exception.UnauthorizedAccessException(
                        "User not authenticated"));
        return projectListPort.findAllByOwnerId(userId);
    }
}
