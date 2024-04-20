package com.meonghae.communityservice.infra.redis;

import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.application.port.S3ServicePort;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.dto.fcm.Fcm;
import com.meonghae.communityservice.dto.s3.S3Request;
import com.meonghae.communityservice.dto.s3.S3Response;
import com.meonghae.communityservice.dto.s3.UserImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class RedisService implements RedisPort {
    private final RedisCacheManager cacheManager;
    private final UserServicePort userService;
    private final S3ServicePort s3Service;

    @Value("${cacheName.getByEmail}")
    private String byEmail;

    @Value("${cacheName.getProfile}")
    private String getProfile;

    @Value("${cacheName.getImages}")
    private String getImages;

    @Value("${cacheName.getFCM}")
    private String getFCM;

    public String getNickname(String email) {
        String nickname = cacheManager.getCache(byEmail).get(email, String.class);
        if(nickname == null) {
            log.info("=========== User Feign 호출 ===========");
            nickname = userService.getNickname(email);
            if(nickname == null) {
                log.info("======== 유저 정보 없음 ========");
                return "탈퇴한 회원";
            }
            cacheManager.getCache(byEmail).put(email, nickname);
        }
        log.info("======== 유저 정보 있음 ========");
        return nickname;
    }

    public String getProfileImage(String email) {
        String url = cacheManager.getCache(getProfile).get(email, String.class);
        if(url == null) {
            log.info("=========== S3 Feign 호출 ===========");
            UserImage dto = s3Service.getUserImage(email);
            if(dto == null) {
                log.info("========= null 리턴 =========");
                return null;
            }
            url = dto.getFileUrl();
            cacheManager.getCache(getProfile).put(email, dto.getFileUrl());
        }
        log.info("========= url 리턴 =========");
        return url;
    }

    public List<S3Response> getReviewImages(Long reviewId) {
        List<S3Response> dtos;
        Cache.ValueWrapper value = cacheManager.getCache(getImages).get(reviewId);
        if (value != null) {
            log.info("========== value 값 존재 ==========");
            dtos = (List<S3Response>) value.get();
        } else {
            log.info("=========== S3 Feign 호출 ===========");
            dtos = s3Service.getImages(new S3Request(reviewId, "REVIEW"));
            if(dtos == null) return null;
            cacheManager.getCache(getImages).put(reviewId, dtos);
        }
        return dtos;
    }

    public String getFcmToken(String email) {
        String fcm = cacheManager.getCache(getFCM).get(email, String.class);
        if(fcm == null) {
            log.info("=========== User Feign 호출 ===========");
            Fcm fcmDto = userService.getFCMToken(email);
            if(fcmDto == null) {
                return null;
            }
            fcm = fcmDto.getFCMToken();
            cacheManager.getCache(getFCM).put(fcmDto.getEmail(), fcmDto.getFCMToken());
        }
        return fcm;
    }
}
