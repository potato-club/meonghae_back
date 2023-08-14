package com.meonghae.userservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    @ApiModelProperty(value = "카카오 Email")
    private String email;

    @ApiModelProperty(value = "회원/비회원 판별용 코드")
    private String responseCode;
}
