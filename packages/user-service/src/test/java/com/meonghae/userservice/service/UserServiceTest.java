package com.meonghae.userservice.service;

import com.meonghae.userservice.core.exception.impl.UnAuthorizedException;
import com.meonghae.userservice.core.jwt.JwtTokenProviderImpl;
import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.domin.user.enums.UserRole;
import com.meonghae.userservice.dto.user.UserRequest;
import com.meonghae.userservice.dto.user.UserResponse;
import com.meonghae.userservice.mock.*;
import com.meonghae.userservice.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserServiceImpl userService;
    private FakeUserRepository fakeUserRepository;
    private FakeRedisService fakeRedisService;
    private JwtTokenProviderImpl jwtTokenProvider;

    @BeforeEach
    void init() {
        Map<String, Object> map = new HashMap<>();

        fakeUserRepository = new FakeUserRepository();
        fakeRedisService = new FakeRedisService(map);

        jwtTokenProvider = JwtTokenProviderImpl.builder()
                .userRepository(fakeUserRepository)
                .redisService(fakeRedisService)
                .secretKey("aaaaaaaaaaa-aaaaaaaaaaaaaaaaaaaaa-aaaaaaaaaaaaaaaa-aaaaaaaaaaaaaa")
                .accessTokenValidTime(180000L)
                .refreshTokenValidTime(180000L)
                .build();

        this.userService = UserServiceImpl.builder()
                .userRepository(fakeUserRepository)
                .fcmTokenRepository(new FakeFCMTokenRepository())
                .redisService(fakeRedisService)
                .jwtTokenProvider(jwtTokenProvider)
                .s3Service(new FakeS3ServiceClient())
                .petServiceClient(new FakePetServiceClient())
                .build();

        User user = User.builder()
                .uid("123-456-789")
                .email("test@test.com")
                .age(25)
                .birth(LocalDate.of(2000, 1, 1))
                .nickname("Test-User")
                .userRole(UserRole.USER)
                .deleted(false)
                .build();

        fakeUserRepository.save(user);
    }

    @Test
    void login_을_성공적으로_마치면_토큰과_200_OK_를_반환한다() {
        // given
        String email = "test@test.com";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        UserResponse userResponse = userService.login(email, request, response);

        // then
        verify(response, times(1)).setHeader(eq("Authorization"), anyString());
        verify(response, times(1)).setHeader(eq("RefreshToken"), anyString());
        assertThat(userResponse.getResponseCode()).isEqualTo("200_OK");
    }

    @Test
    void 회원이_아닐_경우_201_CREATED_를_반환한다() {
        // given
        String email = "test1@test.com";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        UserResponse userResponse = userService.login(email, request, response);

        // then
        assertThat(userResponse.getResponseCode()).isEqualTo("201_CREATED");
    }

    @Test
    void 회원이_아닐_경우_회원_가입을_진행할_수_있다() {
        // given
        MultipartFile file = mock(MultipartFile.class);

        UserRequest userRequest = UserRequest.builder()
                .email("test1@test.com")
                .nickname("Test-User1")
                .birth("20200102")
                .age(25)
                .file(file)
                .build();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        userService.signUp(userRequest, request, response);

        // then
        verify(response, times(1)).setHeader(eq("Authorization"), anyString());
        verify(response, times(1)).setHeader(eq("RefreshToken"), anyString());
        assertThat(fakeUserRepository.findByEmail(userRequest.getEmail())).isPresent();
    }

    @Test
    void 이미_회원인_경우_회원_가입을_진행하면_401_에러를_반환한다() {
        // given
        MultipartFile file = mock(MultipartFile.class);

        UserRequest userRequest = UserRequest.builder()
                .email("test@test.com")
                .nickname("Test-User")
                .birth("20200102")
                .age(25)
                .file(file)
                .build();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        // then
        assertThatThrownBy(() -> {
            userService.signUp(userRequest, request, response);
        }).isInstanceOf(UnAuthorizedException.class)
                .hasMessage("이미 회원이거나 탈퇴 대기 중인 유저입니다.");
    }

    @Test
    void 로그_아웃을_하면_토큰이_만료된다() {
        // given
        String email = "test@test.com";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        userService.login(email, request, response);

        request.addHeader("Authorization", Objects.requireNonNull(response.getHeader("Authorization")).substring(7));
        request.addHeader("RefreshToken", Objects.requireNonNull(response.getHeader("RefreshToken")).substring(7));

        // when
        userService.logout(request);

        // then
        String token = Objects.requireNonNull(request.getHeader("Authorization"));

        fakeRedisService.isTokenInBlacklist(token);
    }
}
