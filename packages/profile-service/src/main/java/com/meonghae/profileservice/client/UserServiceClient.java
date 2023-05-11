package com.meonghae.profileservice.client;

import com.meonghae.profileservice.config.FeignHeaderConfig;
import com.meonghae.profileservice.dto.user.UserDTO;
import com.meonghae.profileservice.dto.user.UserNicknameDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FeignClient(name = "user-service",configuration = {FeignHeaderConfig.class})
public interface UserServiceClient {
  @GetMapping("/send/email")
  String getUserEmail(@RequestHeader("Authorization") String token);

}
