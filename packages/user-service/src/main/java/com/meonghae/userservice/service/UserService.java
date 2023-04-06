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

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final KakaoApi kakaoApi;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public UserResponseDto login(String code, HttpServletRequest request, HttpServletResponse response) {
        String access_token = kakaoApi.getAccessToken(code);
        String email = kakaoApi.getUserInfo(access_token);

        if (userRepository.existsByEmail(email)) {
            String ipAddress = request.getHeader("X-Forwarded-For");
            UserRole userRole = userRepository.findByEmail(email).get().getUserRole();

            String accessToken = jwtTokenProvider.createAccessToken(email, userRole);
            String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole);

            jwtTokenProvider.setHeaderAccessToken(response, accessToken);
            jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

            redisService.setValues(refreshToken, email, ipAddress);

            return UserResponseDto.builder()
                    .responseCode("200_OK")
                    .build();
        }

        return UserResponseDto.builder()
                .email(email)
                .responseCode("201_CREATED")
                .build();
    }

    public void signUp(UserRequestDto userDto) {
        userRepository.save(userDto.toEntity());
    }

    public void update(UserRequestDto userDto, HttpServletRequest request, HttpServletResponse response) {
        String email = this.findByEmailFromAccessToken(request, response);
        User user = userRepository.findByEmail(email).orElseThrow();

        user.update(userDto);
        userRepository.save(user);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        redisService.delValues(jwtTokenProvider.resolveRefreshToken(request));
        jwtTokenProvider.expireToken(this.findByAccessToken(request, response));
    }

    private String findByEmailFromAccessToken(HttpServletRequest request, HttpServletResponse response) {
        if (jwtTokenProvider.resolveAccessToken(request) == null) {
            // accessToken을 재발급 받은 경우
            String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(response));
            return email;
        }

        // accessToken이 정상적인 경우
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        return email;
    }

    private String findByAccessToken(HttpServletRequest request, HttpServletResponse response) {
        if (jwtTokenProvider.resolveAccessToken(request) == null)
            return jwtTokenProvider.resolveAccessToken(response);   // accessToken을 재발급 받은 경우
        return jwtTokenProvider.resolveAccessToken(request);        // accessToken이 정상적인 경우
    }
}