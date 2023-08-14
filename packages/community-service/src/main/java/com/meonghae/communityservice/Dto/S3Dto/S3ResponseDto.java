package com.meonghae.communityservice.Dto.S3Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class S3ResponseDto implements Serializable {
    private String fileName;

    private String fileUrl;

    private String entityType;

    private Long typeId;
}
