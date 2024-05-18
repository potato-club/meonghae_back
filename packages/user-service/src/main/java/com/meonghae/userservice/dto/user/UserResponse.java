package com.meonghae.userservice.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse {
    @ApiModelProperty(value = "카카오 Email")
    private final String email;

    @ApiModelProperty(value = "회원/비회원 판별용 코드")
    private final String responseCode;

    @Builder
    public UserResponse(String email, String responseCode) {
        this.email = email;
        this.responseCode = responseCode;
    }
}
