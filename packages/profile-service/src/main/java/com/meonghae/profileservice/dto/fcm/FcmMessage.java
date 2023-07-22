package com.meonghae.profileservice.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Builder
@AllArgsConstructor
@Getter
public class FcmMessage {
    private String to; //fcm에서 토큰을 to로 기대함
    private Notification notification;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String  title;
        private String  body;
    }

}

