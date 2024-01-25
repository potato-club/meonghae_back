package com.meonghae.profileservice.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FCMResponseDto {
    private String email;

    private String FCMToken;
}