package com.meonghae.communityservice.infra.feign;

import com.meonghae.communityservice.infra.feign.config.FeignHeaderConfig;
import com.meonghae.communityservice.dto.fcm.FcmDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", configuration = {FeignHeaderConfig.class})
public interface UserServiceClient {
    @GetMapping("/send/email")
    String getUserEmail(@RequestHeader("Authorization") String token);

    @GetMapping("/send/{email}")
    String getNickname(@PathVariable(value = "email") String email);

    @GetMapping("/send/token")
    FcmDto getFCMToken(@RequestParam String email);
}
