package com.meonghae.communityservice.Client;

import com.meonghae.communityservice.Dto.UserDto.UserDto;
import com.meonghae.communityservice.Dto.UserDto.UserNicknameDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/user-service/users/uid/{nickname}")
    UserDto getUserId(@PathVariable String nickname);

    @GetMapping("/user-service/users/nickname/{userId}")
    UserNicknameDto getNickname(@PathVariable String userId);
}
