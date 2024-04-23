package com.meonghae.userservice.service.port;

import com.meonghae.userservice.domin.FCMToken.FCMToken;

public interface FCMTokenRepository {

    FCMToken findByEmail(String email);

    void save(FCMToken fcmToken);

    void deleteByEmail(String email);
}
