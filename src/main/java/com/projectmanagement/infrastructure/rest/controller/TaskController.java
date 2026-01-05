package com.projectmanagement.infrastructure.rest.controller;

import com.projectmanagement.application.port.input.CompleteTaskUseCase;
import com.projectmanagement.application.port.input.UpdateTaskUseCase;
import com.projectmanagement.application.port.input.DeleteTaskUseCase;
import com.projectmanagement.infrastructure.rest.dto.request.CreateTaskRequest;
import com.projectmanagement.domain.model.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final CompleteTaskUseCase completeTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    public TaskController(CompleteTaskUseCase completeTaskUseCase,
            UpdateTaskUseCase updateTaskUseCase,
            DeleteTaskUseCase deleteTaskUseCase) {
        this.completeTaskUseCase = completeTaskUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Complete a task", description = "Marks a task as completed. Only owner can do this.")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable UUID id) {
        completeTaskUseCase.completeTask(id);
        return ResponseEntity.ok().build();
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Update task", description = "Updates the task's details. Only owner can do this.")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable UUID id, @RequestBody CreateTaskRequest request) {
        Task task = updateTaskUseCase.updateTask(id, request.getTitle());
        return ResponseEntity.ok(task);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Delete task", description = "Performs a logical delete on a task. Only owner can do this.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        deleteTaskUseCase.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
