package com.meonghae.communityservice.Dto.S3Dto;

import lombok.Data;

@Data
public class UserImageDto {
    private String fileName;

    private String fileUrl;

    private String entityType;

    private String email;
}
