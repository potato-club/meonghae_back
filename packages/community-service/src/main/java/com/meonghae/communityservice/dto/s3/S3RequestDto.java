package com.meonghae.communityservice.dto.s3;

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
