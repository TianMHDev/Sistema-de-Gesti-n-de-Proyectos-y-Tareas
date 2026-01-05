package com.projectmanagement.application.port.input;

import com.projectmanagement.domain.model.Project;
import java.util.List;

public interface GetProjectsUseCase {
    List<Project> getProjects();
}
