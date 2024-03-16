package com.meonghae.userservice.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class UserCancel {

    @ApiModelProperty(value = "회원 탈퇴한 Email")
    private final String email;

    @ApiModelProperty(value = "철회 동의 여부")
    private final boolean agreement;

    public UserCancel(String email, boolean agreement) {
        this.email = email;
        this.agreement = agreement;
    }
}
