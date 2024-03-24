package com.meonghae.userservice.mock;

import io.jsonwebtoken.MalformedJwtException;

import java.util.Map;

public class FakeRedisService {

    private final Map<String, Object> values;

    public FakeRedisService(Map<String, Object> values) {
        this.values = values;
    }

    public void addTokenToBlacklist(String token) {
        values.put(token, Boolean.TRUE);
    }

    public boolean isTokenInBlacklist(String token) {
        if (Boolean.TRUE.equals(values.get(token))) {
            throw new MalformedJwtException("Invalid JWT token");
        }

        return false;
    }
}
