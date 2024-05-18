package com.meonghae.s3fileservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonSerialize
public class FileRequest {

    @ApiModelProperty(value = "엔티티 종류")
    private final EntityType entityType;

    @ApiModelProperty(value = "엔티티 Id 번호")
    private final Long entityId;

    @Builder
    public FileRequest(EntityType entityType, Long entityId) {
        this.entityType = entityType;
        this.entityId = entityId;
    }
}
