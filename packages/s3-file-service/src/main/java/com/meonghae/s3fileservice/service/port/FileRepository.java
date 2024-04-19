package com.meonghae.s3fileservice.service.port;

import com.meonghae.s3fileservice.domain.File;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import com.meonghae.s3fileservice.dto.FileRequest;
import com.meonghae.s3fileservice.dto.FileResponse;
import com.meonghae.s3fileservice.dto.FileUserResponse;

import java.util.List;

public interface FileRepository {

    List<File> findByEntityTypeAndTypeId(EntityType entityType, Long typeId);

    List<File> findByEntityTypeAndEmail(EntityType entityType, String email);

    void save(File file);

    void delete(File file);

    List<FileResponse> getFileList(FileRequest request);

    FileUserResponse getUserProfile(String email);

    FileUserResponse getPetProfile(FileRequest request);

    boolean existsByEntityTypeAndEmail(EntityType entityType, String email);

    boolean existsByEntityTypeAndTypeId(EntityType entityType, Long id);
}
