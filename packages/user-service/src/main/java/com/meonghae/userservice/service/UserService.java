package com.meonghae.userservice.service;

import com.meonghae.userservice.dto.UserRequestDto;
import com.meonghae.userservice.dto.UserResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {

    UserResponseDto login(String code, HttpServletResponse response);

    void loginTest(HttpServletResponse response);

    String sendEmail(HttpServletRequest request);

    void signUp(UserRequestDto userDto);

    void update(UserRequestDto userDto, HttpServletRequest request);

    void logout(HttpServletRequest request);

    void withdrawalMembership(HttpServletRequest request);
}
