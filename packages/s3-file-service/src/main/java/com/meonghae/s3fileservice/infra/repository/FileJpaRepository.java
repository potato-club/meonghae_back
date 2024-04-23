package com.meonghae.s3fileservice.infra.repository;

import com.meonghae.s3fileservice.infra.entity.FileEntity;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileJpaRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findByEntityTypeAndTypeId(EntityType entityType, Long typeId);

    List<FileEntity> findByEntityTypeAndEmail(EntityType entityType, String email);

    boolean existsByEntityTypeAndEmail(EntityType entityType, String email);

    boolean existsByEntityTypeAndTypeId(EntityType entityType, Long id);
}
