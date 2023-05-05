package com.meonghae.communityservice.Client;

import com.meonghae.communityservice.Config.FeignHeaderConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", configuration = {FeignHeaderConfig.class})
public interface UserServiceClient {
    @GetMapping("/send/email")
    String getUserEmail(@RequestHeader("Authorization") String token);

    @GetMapping("/send/{email}")
    String getNickname(@PathVariable(value = "email") String email);
}
