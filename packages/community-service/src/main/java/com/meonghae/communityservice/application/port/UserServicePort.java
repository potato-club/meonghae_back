package com.meonghae.communityservice.application.port;

import com.meonghae.communityservice.dto.fcm.FcmDto;

public interface UserServicePort {
    String getUserEmail(String token);

    String getNickname(String email);

    FcmDto getFCMToken(String email);
}
