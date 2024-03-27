package com.meonghae.userservice.service.port;

import java.util.Map;

public interface RedisService {

    void setValues(String token, String email, String androidId);

    void setValues(String email, String androidId, String accessToken, String refreshToken);

    Map<String, String> getValues(String token);

    void addTokenToBlacklist(String token, long expiration);

    void delValues(String token, String email);
}
