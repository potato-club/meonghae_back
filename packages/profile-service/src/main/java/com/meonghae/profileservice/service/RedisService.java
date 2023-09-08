package com.meonghae.profileservice.service;

import com.meonghae.profileservice.client.UserServiceClient;
import com.meonghae.profileservice.dto.fcm.FCMResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RedisService {
    private final RedisCacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserServiceClient userFeignService;


    @Value("${cacheName.getFCM}")
    private String getFCM;


    public String getFcmToken(String email) {
        String fcmToken = cacheManager.getCache(getFCM).get(email, String.class);

        if ( fcmToken == null ) {
            FCMResponseDto fcmResponseDto = userFeignService.getFCMToken(email);
            this.saveFcmToken(email, fcmResponseDto.getFCMToken());
        } else {
            //만료 시간 재설정
            String cacheKey = getFCM + "::" + email;
            redisTemplate.expire(cacheKey,8, TimeUnit.DAYS);
        }
        return fcmToken;
    }

    private void saveFcmToken(String email, String fcmToken) {
        cacheManager.getCache(getFCM).put(email, fcmToken);
    }
    public void updateFcm(String email, String fcmToken) {
        //redis에 존재하면 교체
        if (cacheManager.getCache(getFCM).get(email, String.class) != null) {
            redisTemplate.opsForValue().set(email,fcmToken);
        }
    }
}
