package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.UserServiceClient;
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

    @Value("${cacheName.getByEmail}")
    private String byEmail;

    public String getNickname(String email) {
        String nickname = cacheManager.getCache(byEmail).get(email, String.class);
        if(nickname == null) {
            log.info("=========== Feign 호출 ===========");
            nickname = userService.getNickname(email);
            if(nickname == null) {
                return "탈퇴한 회원";
            }
            cacheManager.getCache(byEmail).put(email, nickname);
        }
        return nickname;
    }
}
