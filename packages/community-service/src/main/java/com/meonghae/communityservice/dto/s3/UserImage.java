package com.meonghae.communityservice.dto.s3;

import lombok.Data;

@Data
public class UserImage {
    private String fileName;

    private String fileUrl;

    private String entityType;

    private String email;
}
