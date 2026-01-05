package com.projectmanagement.infrastructure.rest.controller;

import com.projectmanagement.application.port.input.ActivateProjectUseCase;
import com.projectmanagement.application.port.input.CreateProjectUseCase;
import com.projectmanagement.application.port.input.CreateTaskUseCase;
import com.projectmanagement.application.port.input.GetProjectsUseCase;
import com.projectmanagement.application.port.input.GetTasksUseCase;
import com.projectmanagement.application.port.input.UpdateProjectUseCase;
import com.projectmanagement.application.port.input.DeleteProjectUseCase;
import com.projectmanagement.domain.model.Project;
import com.projectmanagement.domain.model.Task;
import com.projectmanagement.infrastructure.rest.dto.request.CreateProjectRequest;
import com.projectmanagement.infrastructure.rest.dto.request.CreateTaskRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ActivateProjectUseCase activateProjectUseCase;
    private final CreateProjectUseCase createProjectUseCase;
    private final GetProjectsUseCase getProjectsUseCase;
    private final CreateTaskUseCase createTaskUseCase;
    private final GetTasksUseCase getTasksUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;

    public ProjectController(ActivateProjectUseCase activateProjectUseCase,
            CreateProjectUseCase createProjectUseCase,
            GetProjectsUseCase getProjectsUseCase,
            CreateTaskUseCase createTaskUseCase,
            GetTasksUseCase getTasksUseCase,
            UpdateProjectUseCase updateProjectUseCase,
            DeleteProjectUseCase deleteProjectUseCase) {
        this.activateProjectUseCase = activateProjectUseCase;
        this.createProjectUseCase = createProjectUseCase;
        this.getProjectsUseCase = getProjectsUseCase;
        this.createTaskUseCase = createTaskUseCase;
        this.getTasksUseCase = getTasksUseCase;
        this.updateProjectUseCase = updateProjectUseCase;
        this.deleteProjectUseCase = deleteProjectUseCase;
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Create a new project", description = "Creates a new project for the authenticated user.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project created successfully")
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody CreateProjectRequest request) {
        Project project = createProjectUseCase.createProject(request.getName(), request.getDescription());
        return ResponseEntity.ok(project);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get all projects", description = "Retrieves all projects for the authenticated user.")
    @GetMapping
    public ResponseEntity<List<Project>> getProjects() {
        return ResponseEntity.ok(getProjectsUseCase.getProjects());
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get project tasks", description = "Retrieves all active tasks for a specific project.")
    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasks(@PathVariable UUID projectId) {
        return ResponseEntity.ok(getTasksUseCase.getTasksByProjectId(projectId));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Activate project", description = "Activates a project. Requires at least one active task.")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateProject(@PathVariable UUID id) {
        activateProjectUseCase.activateProject(id);
        return ResponseEntity.ok().build();
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Create a task", description = "Adds a new task to a specific project. Only owner can add tasks.")
    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable UUID projectId, @RequestBody CreateTaskRequest request) {
        Task task = createTaskUseCase.createTask(projectId, request.getTitle(), request.getDescription());
        return ResponseEntity.ok(task);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Update project", description = "Updates the project's details. Only owner can do this.")
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable UUID id, @RequestBody CreateProjectRequest request) {
        Project project = updateProjectUseCase.updateProject(id, request.getName());
        return ResponseEntity.ok(project);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Delete project", description = "Performs a logical delete on a project. Only owner can do this.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        deleteProjectUseCase.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
