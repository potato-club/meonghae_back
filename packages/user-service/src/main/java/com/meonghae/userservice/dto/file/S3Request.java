package com.meonghae.userservice.dto.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class S3Request {

    @ApiModelProperty(value = "사용자 Email", example = "test@test.com")
    private final String email;

    @ApiModelProperty(value = "엔티티 타입", example = "USER")
    private final String entityType;

    @Builder
    public S3Request(String email, String entityType) {
        this.email = email;
        this.entityType = entityType;
    }
}
