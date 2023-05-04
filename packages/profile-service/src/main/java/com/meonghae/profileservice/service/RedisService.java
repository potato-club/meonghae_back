package com.meonghae.profileservice.service;

import com.meonghae.profileservice.client.UserServiceClient;
import com.meonghae.profileservice.dto.user.UserDTO;
import com.meonghae.profileservice.dto.user.UserNicknameDTO;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.BadRequestException;
import com.meonghae.profileservice.error.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import static com.meonghae.profileservice.error.ErrorCode.USER_NOT_FOUND;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RedisService {
    private final RedisCacheManager cacheManager;
    private final UserServiceClient userServiceClient;

//    @Value("${cacheName.getByAT}")
//    private String byAT;
//    @Value("${cacheName.getByUid}")
//    private String byUserId;
//    @Value("${cacheName.getByName}")
//    private String byNickname;

    @Transactional
    public String getUserEmail(HttpServletRequest request) {
        String userEmail = userServiceClient.getUserEmail(request);
        if (userEmail == null) {
            throw new UnAuthorizedException(ErrorCode.CANT_READ_TOKEN, ErrorCode.CANT_READ_TOKEN.getMessage());
        }
        return userEmail;
    }

}


