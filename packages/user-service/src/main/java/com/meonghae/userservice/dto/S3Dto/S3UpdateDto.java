package com.meonghae.userservice.dto.S3Dto;

import com.meonghae.userservice.enums.FileEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class S3UpdateDto {
    private String fileName;

    private String fileUrl;

    private FileEnum entityType;

    private Long entityId;

    private String email;

    private boolean deleted;
}
