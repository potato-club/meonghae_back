package com.meonghae.profileservice.service;

import com.meonghae.profileservice.client.UserServiceClient;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FeignService {
    private final UserServiceClient userServiceClient;

    @Transactional
    public String getUserEmail(String token) {
        String userEmail = userServiceClient.getUserEmail(token);
        if (userEmail == null) {
            throw new UnAuthorizedException(ErrorCode.CANT_READ_TOKEN, ErrorCode.CANT_READ_TOKEN.getMessage());
        }
        return userEmail;
    }

}


