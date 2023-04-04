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

    // RefreshToken, email, IP Address 설정
    public void setValues(String token, String email, String ipAddress) {
        ValueOperations<String, Map<String, String>> values = redisTemplate.opsForValue();
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("ipAddress", ipAddress);
        values.set(token, map, Duration.ofDays(7));  // 7일 뒤 메모리에서 삭제된다.
    }

    // 키값으로 벨류 가져오기
    public Map<String, String> getValues(String token){
        ValueOperations<String, Map<String, String>> values = redisTemplate.opsForValue();
        return values.get(token);
    }

    public void addTokenToBlacklist(String token, long expiration) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token, true, expiration, TimeUnit.MILLISECONDS);
    }

    // RefreshToken, email, IP Address 삭제
    public void delValues(String token) {
        redisTemplate.delete(token);
    }
}
