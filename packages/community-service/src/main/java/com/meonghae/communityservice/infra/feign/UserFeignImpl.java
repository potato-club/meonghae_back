package com.meonghae.communityservice.infra.feign;

import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.dto.fcm.Fcm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserFeignImpl implements UserServicePort {

    private final UserServiceClient serviceClient;

    @Override
    public String getUserEmail(String token) {
        return serviceClient.getUserEmail(token);
    }

    @Override
    public String getNickname(String email) {
        log.info("데이터 대기");
        String nickname = serviceClient.getNickname(email);
        log.info("데이터 반환 완료");
        return nickname;
    }

    @Override
    public Fcm getFCMToken(String email) {
        return serviceClient.getFCMToken(email);
    }
}
