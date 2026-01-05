package com.projectmanagement.infrastructure.adapter.persistence.repository;

import com.projectmanagement.infrastructure.adapter.persistence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface SpringDataProjectRepository extends JpaRepository<ProjectEntity, UUID> {
    List<ProjectEntity> findByDeletedFalse();

    List<ProjectEntity> findByOwnerIdAndDeletedFalse(UUID ownerId);
}
