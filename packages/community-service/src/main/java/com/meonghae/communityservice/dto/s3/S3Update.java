package com.meonghae.communityservice.dto.s3;

import lombok.Getter;

@Getter
public class S3Update {
    private String fileName;

    private String fileUrl;

    private String entityType;

    private Long entityId;

    private String email;

    private boolean deleted;

    public S3Update(S3Response responseDto) {
        this.fileName = responseDto.getFileName();
        this.fileUrl = responseDto.getFileUrl();
        this.entityType = responseDto.getEntityType();
        this.entityId = responseDto.getTypeId();
        this.email = null;
        this.deleted = true;
    }
}
