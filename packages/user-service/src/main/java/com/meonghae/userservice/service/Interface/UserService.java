package com.meonghae.userservice.service.Interface;

import com.meonghae.userservice.dto.UserMyPageDto;
import com.meonghae.userservice.dto.UserRequestDto;
import com.meonghae.userservice.dto.UserResponseDto;
import com.meonghae.userservice.dto.UserUpdateDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {

    UserResponseDto login(String code, HttpServletResponse response);

    String sendEmail(String token);

    String sendNickname(String email);

    UserMyPageDto viewMyPage(HttpServletRequest request);

    void signUp(UserRequestDto userDto);

    void update(UserUpdateDto userDto, HttpServletRequest request);

    void logout(HttpServletRequest request);

    void withdrawalMembership(HttpServletRequest request);
}
