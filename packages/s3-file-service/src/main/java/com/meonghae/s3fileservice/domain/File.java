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

    private EntityType entityType;

    private Long typeId;

    private String email;

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

    public void uploadForData(FileRequest request) {
        this.entityType = request.getEntityType();
        this.typeId = request.getEntityId();
    }

    public void uploadForUser(FileUser user) {
        this.entityType = user.getEntityType();
        this.email = user.getEmail();
    }

    public void update(FileUpdate update) {
        this.entityType = update.getEntityType();
        this.typeId = update.getEntityId();
        this.email = update.getEmail();
    }
}
