package com.meonghae.communityservice.Dto.S3Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class S3RequestDto {
    private Long entityId;
    private String entityType;
}
