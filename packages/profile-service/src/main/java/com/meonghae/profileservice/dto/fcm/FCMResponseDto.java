package com.meonghae.profileservice.dto.fcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FCMResponseDto {
    private String email;
    @JsonProperty("FCMToken")
    private String FCMToken;
}