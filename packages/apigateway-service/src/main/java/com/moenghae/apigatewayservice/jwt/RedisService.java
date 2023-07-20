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

    public void setValues(String token, String email) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        operations.set(token, map, Duration.ofDays(7)); // 7일 뒤 메모리에서 삭제됨
    }

    public void setAndroidId(String email, String androidId) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Map<String, String> map = new HashMap<>();
        map.put("androidId", androidId);
        operations.set(email, map, Duration.ofDays(7)); // 7일 뒤 메모리에서 삭제됨
    }

    public boolean isRefreshTokenValid(String token) {
        Map<String, String> values = getValues(token);
        return !values.isEmpty();
    }

    public boolean isAndroidIdValid(String token) {
        Map<String, String> tokenValue = getValues(token);
        String email = tokenValue.get("email");

        if (email != null) {
            Map<String, String> emailValue = getValues(email);
            return emailValue.containsKey("androidId");
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

    public void delValues(String token) {
        redisTemplate.delete(token);
    }
}
