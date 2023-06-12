package com.meonghae.userservice.dto.S3Dto;

import lombok.Data;

@Data
public class S3UpdateDto {
    private String fileName;

    private String fileUrl;

    private String entityType;

    private Long entityId;

    private String email;

    private boolean deleted;
}
