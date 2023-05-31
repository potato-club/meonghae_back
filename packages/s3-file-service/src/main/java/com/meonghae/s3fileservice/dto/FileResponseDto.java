package com.meonghae.s3fileservice.dto;

import com.meonghae.s3fileservice.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileResponseDto {

    private String fileName;

    private String fileUrl;

    private EntityType entityType;

    private Long typeId;

}
