package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.UserServiceClient;
import com.meonghae.communityservice.Dto.UserDto.UserDto;
import com.meonghae.communityservice.Dto.UserDto.UserNicknameDto;
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

    @Value("${cacheName.getByUid}")
    private String byUid;
    @Value("${cacheName.getByName}")
    private String byNickname;

    @Transactional
    public String getNickname(String uuid) {
        String nickname = cacheManager.getCache(byUid).get(uuid, String.class);
        if(nickname == null) {
            UserNicknameDto dto = userService.getNickname(uuid);
            if(dto == null) {
                throw new BoardException(ErrorCode.BAD_REQUEST, "User is not exist");
            }
            nickname = dto.getNickname();
            cacheManager.getCache(byUid).put(uuid, nickname);
        }
        return nickname;
    }

    @Transactional
    public String getUserId(String nickname) {
        String uuid = cacheManager.getCache(byNickname).get(nickname, String.class);
        if(uuid == null) {
            UserDto dto = userService.getUserId(nickname);
            if(dto == null) {
                throw new BoardException(ErrorCode.BAD_REQUEST, "User is not exist");
            }
            uuid = dto.getUid();
            cacheManager.getCache(byNickname).put(nickname, uuid);
        }
        return uuid;
    }
}
