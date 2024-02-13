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
        String cacheKey = getFCM + "::" + email;
        String fcmToken = cacheManager.getCache(getFCM).get(cacheKey, String.class);
        log.info("redis-34: "+fcmToken);
        if ( fcmToken == null ) {
            try {
                FCMResponseDto fcmResponseDto = userFeignService.getFCMToken(email);
                log.info("redis-37: "+fcmResponseDto.getFcmToken());
                this.saveFcmToken(email, fcmResponseDto.getFcmToken());
                return fcmResponseDto.getFcmToken();
            } catch (RuntimeException e) {
                System.out.println(e.getMessage() + "\n\n" + e.getCause());
            }
        } else {
            //만료 시간 재설정
            redisTemplate.expire(cacheKey,8, TimeUnit.DAYS);
        }
        return fcmToken;
    }

    private void saveFcmToken(String email, String fcmToken) {
        log.info("redis-48");
        String cacheKey = getFCM+"::"+email;
        cacheManager.getCache(getFCM).put(cacheKey, fcmToken);
    }
    public void updateFcm(String email, String fcmToken) {
        //redis에 존재하면 교체
        if (cacheManager.getCache(getFCM).get(email, String.class) != null) {
            redisTemplate.opsForValue().set(email,fcmToken);
        }
    }
}
