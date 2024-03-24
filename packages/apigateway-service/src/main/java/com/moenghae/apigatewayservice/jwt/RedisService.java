package com.moenghae.apigatewayservice.jwt;

import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    // RefreshToken, email, IP Address 가져오기
    public Map<String, String> getValues(String token){
        ValueOperations<String, Map<String, String>> values = redisTemplate.opsForValue();
        return values.get(token);
    }

    public boolean isRefreshTokenValid(String token) {
        Map<String, String> values = getValues(token);
        return !values.isEmpty();
    }

    public boolean isAndroidIdValid(String token, String androidId) {
        Map<String, String> tokenValue = getValues(token);
        String email = tokenValue.get("email");

        if (email != null) {
            Map<String, String> emailValue = getValues(email);
            return emailValue.get("androidId").equals(androidId);
        } else {
            return false;
        }
    }

    public boolean isTokenInBlacklist(String token) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
            throw new MalformedJwtException("Invalid JWT token");
        }
        return false;
    }
}
