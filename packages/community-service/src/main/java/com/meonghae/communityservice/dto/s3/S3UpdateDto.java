package com.meonghae.communityservice.dto.s3;

import lombok.Getter;

@Getter
public class S3UpdateDto {
    private String fileName;

    private String fileUrl;

    private String entityType;

    private Long entityId;

    private String email;

    private boolean deleted;

    public S3UpdateDto(S3ResponseDto responseDto) {
        this.fileName = responseDto.getFileName();
        this.fileUrl = responseDto.getFileUrl();
        this.entityType = responseDto.getEntityType();
        this.entityId = responseDto.getTypeId();
        this.email = null;
        this.deleted = true;
    }
}
