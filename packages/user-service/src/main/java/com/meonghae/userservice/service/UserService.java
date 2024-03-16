package com.meonghae.userservice.service;

import com.meonghae.userservice.dto.fcmtoken.FCMResponse;
import com.meonghae.userservice.dto.user.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {

    UserResponse login(String code, HttpServletRequest request, HttpServletResponse response);

    String sendEmail(String token);

    String sendNickname(String email);

    FCMResponse sendFCMToken(String email);

    UserMyPage viewMyPage(HttpServletRequest request);

    void signUp(UserRequest userDto, HttpServletRequest request, HttpServletResponse response);

    void update(UserUpdate userDto, HttpServletRequest request);

    void logout(HttpServletRequest request);

    void withdrawalMembership(HttpServletRequest request);

    void cancelWithdrawal(String email, boolean agreement);

    void reissueToken(HttpServletRequest request, HttpServletResponse response);
}
