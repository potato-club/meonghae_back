package com.meonghae.s3fileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUserResponseDto {

    private String fileName;

    private String fileUrl;

    private String entityType;

    private String email;

}
