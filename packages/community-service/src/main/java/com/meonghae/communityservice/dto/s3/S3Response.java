package com.meonghae.communityservice.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class S3Response implements Serializable {
    private String fileName;

    private String fileUrl;

    private String entityType;

    private Long typeId;
}
