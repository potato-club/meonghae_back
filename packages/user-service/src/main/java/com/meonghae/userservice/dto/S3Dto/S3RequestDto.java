package com.meonghae.userservice.dto.S3Dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class S3RequestDto {

    @ApiModelProperty(value = "사용자 Email", example = "test@test.com")
    private String email;

    @ApiModelProperty(value = "엔티티 타입", example = "USER")
    private String entityType;
}
