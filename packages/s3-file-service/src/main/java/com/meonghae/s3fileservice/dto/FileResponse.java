package com.meonghae.s3fileservice.dto;

import com.meonghae.s3fileservice.domain.enums.EntityType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FileResponse {

    private final String fileName;

    private final String fileUrl;

    private final EntityType entityType;

    private final Long typeId;

    @Builder
    public FileResponse(String fileName, String fileUrl, EntityType entityType, Long typeId) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.entityType = entityType;
        this.typeId = typeId;
    }
}
