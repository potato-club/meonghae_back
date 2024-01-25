package com.meonghae.profileservice.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Builder
@AllArgsConstructor
@Getter
public class FcmMessage {
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private String token; // "to" 대신 "token" 사용
        private Notification notification;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
    }
}

//public class FcmMessage {
//    private String to; //fcm에서 토큰을 to로 기대함
//    private Notification notification;
//
//    @Builder
//    @AllArgsConstructor
//    @Getter
//    public static class Notification {
//        private String  title;
//        private String  body;
//    }