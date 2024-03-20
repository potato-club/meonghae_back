package com.meonghae.userservice.domin.FCMToken;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FCMToken {

    private final Long id;

    private final String token;

    private final String email;

    @Builder
    public FCMToken(Long id, String email, String token) {
        this.id = id;
        this.email = email;
        this.token = token;
    }

    public FCMToken update(String token) {
        return FCMToken.builder()
                .id(id)
                .email(email)
                .token(token)
                .build();
    }
}
