package com.projectmanagement.infrastructure.adapter.persistence.repository;

import com.projectmanagement.infrastructure.adapter.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataTaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByProjectIdAndDeletedFalse(UUID projectId);
}
