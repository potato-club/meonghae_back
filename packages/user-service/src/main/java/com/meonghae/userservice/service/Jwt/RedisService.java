package com.meonghae.userservice.service.Jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    // RefreshToken, email 설정
    public void setValues(String token, String email, String androidId) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("androidId", androidId);
        operations.set(token, map, Duration.ofDays(7)); // 7일 뒤 메모리에서 삭제됨
    }

    // RefreshToken, Android-Id 설정
    public void setValues(String email, String androidId, String accessToken, String refreshToken) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Map<String, String> map = new HashMap<>();
        map.put("androidId", androidId);
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);
        operations.set(email, map, Duration.ofDays(7)); // 7일 뒤 메모리에서 삭제됨
    }

    // 키값으로 벨류 가져오기
    public Map<String, String> getValues(String token) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Object object = operations.get(token);
        if (object != null && object instanceof Map) {
            return (Map<String, String>) object;
        }
        return null;
    }

    public void addTokenToBlacklist(String token, long expiration) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token, true, expiration, TimeUnit.MILLISECONDS);
    }

    // RefreshToken, Android-Id 삭제
    public void delValues(String token, String email) {
        redisTemplate.delete(token);
        redisTemplate.delete(email);
    }
}
