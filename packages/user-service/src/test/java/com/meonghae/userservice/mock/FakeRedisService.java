package com.meonghae.userservice.mock;

import com.meonghae.userservice.service.port.RedisService;
import io.jsonwebtoken.MalformedJwtException;

import java.util.HashMap;
import java.util.Map;

public class FakeRedisService implements RedisService {

    private final Map<String, Object> values;

    public FakeRedisService(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public void addTokenToBlacklist(String token, long expiration) {
        values.put(token, Boolean.TRUE);
    }

    public boolean isTokenInBlacklist(String token) {
        if (Boolean.TRUE.equals(values.get(token))) {
            throw new MalformedJwtException("Invalid JWT token");
        }

        return false;
    }

    @Override
    public void setValues(String token, String email, String androidId) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("androidId", androidId);

        values.put(token, map);
    }

    @Override
    public void setValues(String email, String androidId, String accessToken, String refreshToken) {
        Map<String, String> map = new HashMap<>();
        map.put("androidId", androidId);
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);

        values.put(email, map);
    }

    @Override
    public Map<String, String> getValues(String token) {
        Object object = values.get(token);

        if (object instanceof Map) {
            return (Map<String, String>) object;
        }

        return null;
    }

    @Override
    public void delValues(String token, String email) {
        values.remove(token);
        values.remove(email);
    }
}
