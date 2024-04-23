package com.meonghae.s3fileservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonSerialize
public class FileUser {

    @ApiModelProperty(value = "엔티티 종류")
    private final EntityType entityType;

    @ApiModelProperty(value = "유저 Email")
    private final String email;

    @Builder
    public FileUser(EntityType entityType, String email) {
        this.entityType = entityType;
        this.email = email;
    }
}
