package com.meonghae.userservice.service.impl;

import com.meonghae.userservice.dto.UserRequestDto;
import com.meonghae.userservice.dto.UserResponseDto;
import com.meonghae.userservice.entity.User;
import com.meonghae.userservice.enums.UserRole;
import com.meonghae.userservice.jwt.JwtTokenProvider;
import com.meonghae.userservice.repository.UserRepository;
import com.meonghae.userservice.service.Jwt.RedisService;
import com.meonghae.userservice.service.KakaoApi;
import com.meonghae.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KakaoApi kakaoApi;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    public UserResponseDto login(String code, HttpServletResponse response) {
        String access_token = kakaoApi.getAccessToken(code);
        String email = kakaoApi.getUserInfo(access_token);

        if (userRepository.existsByEmail(email)) {
            UserRole userRole = userRepository.findByEmail(email).get().getUserRole();

            String accessToken = jwtTokenProvider.createAccessToken(email, userRole);
            String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole);

            jwtTokenProvider.setHeaderAccessToken(response, accessToken);
            jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

            redisService.setValues(refreshToken, email);

            return UserResponseDto.builder()
                    .responseCode("200_OK")
                    .build();
        }

        return UserResponseDto.builder()
                .email(email)
                .responseCode("201_CREATED")
                .build();
    }

    @Override
    public void loginTest(HttpServletResponse response) {
        String email = "test1234@test.com";
        UserRole userRole = UserRole.USER;

        String accessToken = jwtTokenProvider.createAccessToken(email, userRole);
        String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole);

        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

        redisService.setValues(refreshToken, email);
    }

    @Override
    public String sendEmail(HttpServletRequest request) {
        return this.findByEmailFromAccessToken(request);
    }

    @Override
    public void signUp(UserRequestDto userDto) {
        userRepository.save(userDto.toEntity());
    }

    @Override
    public void update(UserRequestDto userDto, HttpServletRequest request) {
        String email = this.findByEmailFromAccessToken(request);
        User user = userRepository.findByEmail(email).orElseThrow();

        user.update(userDto);
        userRepository.save(user);
    }

    @Override
    public void logout(HttpServletRequest request) {
        redisService.delValues(jwtTokenProvider.resolveRefreshToken(request));
        jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(request));
    }

    @Override
    public void withdrawalMembership(HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();

        user.setDeleted(true);
        this.logout(request);
    }

    public String findByEmailFromAccessToken(HttpServletRequest request) {
        return jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
    }
}
