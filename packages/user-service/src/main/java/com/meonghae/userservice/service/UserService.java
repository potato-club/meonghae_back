package com.meonghae.userservice.service;

import com.meonghae.userservice.dto.UserRequestDto;
import com.meonghae.userservice.dto.UserResponseDto;
import com.meonghae.userservice.entity.User;
import com.meonghae.userservice.enums.UserRole;
import com.meonghae.userservice.jwt.JwtTokenProvider;
import com.meonghae.userservice.repository.UserRepository;
import com.meonghae.userservice.service.Jwt.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {

    UserResponseDto login(String code, HttpServletResponse response);

    void signUp(UserRequestDto userDto);

    void update(UserRequestDto userDto, HttpServletRequest request);

    void logout(HttpServletRequest request);

    String findByEmailFromAccessToken(HttpServletRequest request);

    String findByAccessToken(HttpServletRequest request);
}
