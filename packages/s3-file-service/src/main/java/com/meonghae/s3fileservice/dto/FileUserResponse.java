package com.meonghae.s3fileservice.dto;

import com.meonghae.s3fileservice.domain.enums.EntityType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FileUserResponse {

    private final String fileName;

    private final String fileUrl;

    private final EntityType entityType;

    private final String email;

    @Builder
    public FileUserResponse(String fileName, String fileUrl,
                            EntityType entityType, String email) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.entityType = entityType;
        this.email = email;
    }
}
