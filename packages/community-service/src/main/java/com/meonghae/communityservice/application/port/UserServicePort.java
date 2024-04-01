package com.meonghae.communityservice.application.port;

import com.meonghae.communityservice.dto.fcm.Fcm;

public interface UserServicePort {
    String getUserEmail(String token);

    String getNickname(String email);

    Fcm getFCMToken(String email);
}
