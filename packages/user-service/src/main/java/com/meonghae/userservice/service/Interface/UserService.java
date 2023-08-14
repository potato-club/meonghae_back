package com.meonghae.userservice.service.Interface;

import com.meonghae.userservice.dto.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {

    UserResponseDto login(String code, HttpServletRequest request, HttpServletResponse response);

    String sendEmail(String token);

    String sendNickname(String email);

    FCMResponseDto sendFCMToken(String email);

    UserMyPageDto viewMyPage(HttpServletRequest request);

    void signUp(UserRequestDto userDto, HttpServletRequest request, HttpServletResponse response);

    void update(UserUpdateDto userDto, HttpServletRequest request);

    void logout(HttpServletRequest request);

    void withdrawalMembership(HttpServletRequest request);

    void cancelWithdrawal(String email, boolean agreement);

    void reissueToken(HttpServletRequest request, HttpServletResponse response);
}
