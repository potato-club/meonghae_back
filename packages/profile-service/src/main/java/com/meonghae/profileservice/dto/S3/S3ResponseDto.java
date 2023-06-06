package com.meonghae.profileservice.dto.S3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3ResponseDto {

    private String fileName;

    private String fileUrl;

    private String entityType;

    private Long typeId;

}
