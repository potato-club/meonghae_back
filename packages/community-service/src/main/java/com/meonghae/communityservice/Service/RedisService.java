package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.S3ServiceClient;
import com.meonghae.communityservice.Client.UserServiceClient;
import com.meonghae.communityservice.Dto.S3Dto.S3RequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3ResponseDto;
import com.meonghae.communityservice.Dto.S3Dto.UserImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RedisService {
    private final RedisCacheManager cacheManager;
    private final UserServiceClient userService;
    private final S3ServiceClient s3Service;

    @Value("${cacheName.getByEmail}")
    private String byEmail;

    @Value("${cacheName.getProfile}")
    private String getProfile;

    @Value("${cacheName.getImages}")
    private String getImages;

    public String getNickname(String email) {
        String nickname = cacheManager.getCache(byEmail).get(email, String.class);
        if(nickname == null) {
            log.info("=========== User Feign 호출 ===========");
            nickname = userService.getNickname(email);
            if(nickname == null) {
                return "탈퇴한 회원";
            }
            cacheManager.getCache(byEmail).put(email, nickname);
        }
        return nickname;
    }

    public String getProfileImage(String email) {
        String url = cacheManager.getCache(getProfile).get(email, String.class);
        if(url == null) {
            log.info("=========== S3 Feign 호출 ===========");
            UserImageDto dto = s3Service.getUserImage(email);
            if(dto == null) {
                return null;
            }
            url = dto.getFileUrl();
            cacheManager.getCache(getProfile).put(email, dto.getFileUrl());
        }
        return url;
    }

    public List<S3ResponseDto> getReviewImages(Long reviewId) {
        List<S3ResponseDto> dtos;
        Cache.ValueWrapper value = cacheManager.getCache(getImages).get(reviewId);
        if (value != null) {
            dtos = (List<S3ResponseDto>) value.get();
        } else {
            log.info("=========== S3 Feign 호출 ===========");
            dtos = s3Service.getImages(new S3RequestDto(reviewId, "REVIEW"));
            if(dtos == null) return null;
            cacheManager.getCache(getImages).put(reviewId, dtos);
        }
        return dtos;
    }
}
