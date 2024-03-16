package com.meonghae.s3fileservice.domain;

import com.meonghae.s3fileservice.domain.enums.EntityType;
import com.meonghae.s3fileservice.dto.FileRequest;
import com.meonghae.s3fileservice.dto.FileUpdate;
import com.meonghae.s3fileservice.dto.FileUser;
import lombok.Builder;
import lombok.Getter;

@Getter
public class File {

    private final Long id;

    private final String fileName;

    private final String fileUrl;

    private final EntityType entityType;

    private final Long typeId;

    private final String email;

    @Builder
    public File(Long id, String fileName, String fileUrl,
                EntityType entityType, Long typeId, String email) {
        this.id = id;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.entityType = entityType;
        this.typeId = typeId;
        this.email = email;
    }

    public void updateForData(FileRequest request) {
        File.builder()
                .id(id)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .entityType(request.getEntityType())
                .typeId(request.getEntityId())
                .email(email);
    }

    public void updateForUser(FileUser user) {
        File.builder()
                .id(id)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .entityType(user.getEntityType())
                .typeId(typeId)
                .email(user.getEmail());
    }

    public void update(FileUpdate update) {
        File.builder()
                .id(id)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .entityType(update.getEntityType())
                .typeId(update.getEntityId())
                .email(update.getEmail());
    }
}
