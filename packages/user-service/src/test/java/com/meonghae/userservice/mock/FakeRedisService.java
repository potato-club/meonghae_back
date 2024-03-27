package com.meonghae.userservice.mock;

import com.meonghae.userservice.service.port.RedisService;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

@Qualifier("fakeRedisService")
public class FakeRedisService implements RedisService {

    private final Map<String, Object> values;

    public FakeRedisService(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public void addTokenToBlacklist(String token, long expiration) {
        values.put(token, Boolean.TRUE);
    }

    public void isTokenInBlacklist(String token) {
        if (Boolean.TRUE.equals(values.get(token))) {
            throw new MalformedJwtException("Invalid JWT token");
        }
    }

    @Override
    public void setValues(String token, String email, String androidId) {

    }

    @Override
    public void setValues(String email, String androidId, String accessToken, String refreshToken) {

    }

    @Override
    public Map<String, String> getValues(String token) {
        return null;
    }

    @Override
    public void delValues(String token, String email) {

    }
}
