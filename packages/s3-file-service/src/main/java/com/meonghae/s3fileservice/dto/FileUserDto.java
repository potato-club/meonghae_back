package com.meonghae.s3fileservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.meonghae.s3fileservice.enums.EntityType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonSerialize
public class FileUserDto {

    @ApiModelProperty(value = "엔티티 종류")
    private EntityType entityType;

    @ApiModelProperty(value = "유저 Email")
    private String email;
}
