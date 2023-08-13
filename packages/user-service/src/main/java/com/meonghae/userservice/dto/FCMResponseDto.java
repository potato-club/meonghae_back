package com.meonghae.userservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FCMResponseDto {

    @ApiModelProperty(value = "email")
    private String email;

    @ApiModelProperty(value = "FCMToken")
    private String FCMToken;
}
