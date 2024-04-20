package com.meonghae.communityservice.infra.feign;

import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.dto.fcm.FcmDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFeignImpl implements UserServicePort {

    private final UserServiceClient serviceClient;

    @Override
    public String getUserEmail(String token) {
        return serviceClient.getUserEmail(token);
    }

    @Override
    public String getNickname(String email) {
        return serviceClient.getNickname(email);
    }

    @Override
    public FcmDto getFCMToken(String email) {
        return serviceClient.getFCMToken(email);
    }
}
