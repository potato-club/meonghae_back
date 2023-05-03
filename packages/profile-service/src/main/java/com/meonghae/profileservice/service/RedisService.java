package com.meonghae.profileservice.service;

import com.meonghae.profileservice.client.UserServiceClient;
import com.meonghae.profileservice.dto.user.UserDTO;
import com.meonghae.profileservice.dto.user.UserNicknameDTO;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.BadRequestException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.meonghae.profileservice.error.ErrorCode.USER_NOT_FOUND;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RedisService {
    private final RedisCacheManager cacheManager;
    private final UserServiceClient userServiceClient;

    @Value("${cacheName.getByUid}")
    private String byUserId;
    @Value("${cacheName.getByName}")
    private String byNickname;

    @Transactional
    public String getByNickname(String uuid){
        String nickName = cacheManager.getCache(byUserId).get(uuid,String.class);
        if (nickName == null){
           UserNicknameDTO dto = userServiceClient.getNickname(uuid);
           if (dto == null){
               throw new BadRequestException(ErrorCode.NOT_FOUND_PET,ErrorCode.NOT_FOUND_PET.getMessage());
           }
           nickName = dto.getNickname();
           cacheManager.getCache(byUserId).put(uuid,nickName);
        }
        return nickName;
    }
    @Transactional
    public String getUserId(String nickname) {
        String uuid = cacheManager.getCache(byNickname).get(nickname, String.class);
        if(uuid == null) {
            UserDTO dto = userServiceClient.getUserId(nickname);
            if(dto == null) {
                throw new BadRequestException(ErrorCode.NOT_FOUND_PET,ErrorCode.NOT_FOUND_PET.getMessage());
            }
            uuid = dto.getUid();
            cacheManager.getCache(byNickname).put(nickname, uuid);
        }
        return uuid;
    }
}
