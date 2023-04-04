package com.moenghae.apigatewayservice.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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

    public boolean isRefreshTokenValid(String token, String ipAddress) {
        Map<String, String> values = getValues(token);
        if (values == null) {
            return false;
        }
        String storedIpAddress = values.get("ipAddress");
        return ipAddress.equals(storedIpAddress);
    }

    public boolean isTokenInBlacklist(String token) {
        if (redisTemplate.hasKey(token)) {
            throw new InvalidTokenException("Invalid access: token in blacklist");
        }
        return false;
    }
}
