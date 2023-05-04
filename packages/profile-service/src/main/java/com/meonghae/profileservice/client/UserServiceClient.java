package com.meonghae.profileservice.client;

import com.meonghae.profileservice.dto.user.UserDTO;
import com.meonghae.profileservice.dto.user.UserNicknameDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FeignClient(name = "user-service")
public interface UserServiceClient {
//  @GetMapping("/user-service/users/uid/{nickname}")
//  UserDTO getUserId(@PathVariable String nickname);
//
//  @GetMapping("/user-service/users/nickname/{userId}")
//  UserNicknameDTO getNickname(@PathVariable String userId);

  @GetMapping("/user-service/send/email")
  String getUserEmail(HttpServletRequest request);

}
