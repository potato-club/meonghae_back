package com.meonghae.userservice.dto.fcmtoken;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FCMResponse {

    @ApiModelProperty(value = "email")
    private final String email;

    @ApiModelProperty(value = "fcmToken")
    private final String fcmToken;

    @Builder
    public FCMResponse(String email, String fcmToken) {
        this.email = email;
        this.fcmToken = fcmToken;
    }
}
