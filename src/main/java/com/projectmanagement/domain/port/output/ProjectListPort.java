package com.projectmanagement.domain.port.output;

import com.projectmanagement.domain.model.Project;
import java.util.List;

import java.util.UUID;

public interface ProjectListPort {
    List<Project> findAllByOwnerId(UUID ownerId);
}
