package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.S3ServiceClient;
import com.meonghae.communityservice.Client.UserServiceClient;
import com.meonghae.communityservice.Dto.S3Dto.UserImageDto;
import com.meonghae.communityservice.Exception.Custom.BoardException;
import com.meonghae.communityservice.Exception.Error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
            url = dto.getFileUrl();
            if(url == null) {
                return null;
            }
            cacheManager.getCache(getProfile).put(email, url);
        }
        return url;
    }
}
