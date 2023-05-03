package com.meonghae.profileservice.client;

import com.meonghae.profileservice.dto.user.UserDTO;
import com.meonghae.profileservice.dto.user.UserNicknameDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
  @GetMapping("/user-service/users/uid/{nickname}")
  UserDTO getUserId(@PathVariable String nickname);

  @GetMapping("user-service/users/nickname/{userId}")
  UserNicknameDTO getNickname(@PathVariable String userId);
}
