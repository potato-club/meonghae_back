package com.meonghae.s3fileservice.repository;

import com.meonghae.s3fileservice.entity.File;
import com.meonghae.s3fileservice.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByEntityTypeAndTypeId(EntityType entityType, Long typeId);

    List<File> findByEntityTypeAndEmail(EntityType entityType, String email);

}
