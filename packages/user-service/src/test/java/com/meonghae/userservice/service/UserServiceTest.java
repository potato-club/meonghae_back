package com.meonghae.userservice.service;

import com.meonghae.userservice.core.exception.ErrorCode;
import com.meonghae.userservice.core.exception.impl.NotFoundException;
import com.meonghae.userservice.core.exception.impl.UnAuthorizedException;
import com.meonghae.userservice.core.jwt.JwtTokenProviderImpl;
import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.domin.user.enums.UserRole;
import com.meonghae.userservice.dto.user.UserMyPage;
import com.meonghae.userservice.dto.user.UserRequest;
import com.meonghae.userservice.dto.user.UserResponse;
import com.meonghae.userservice.dto.user.UserUpdate;
import com.meonghae.userservice.mock.*;
import com.meonghae.userservice.service.user.UserServiceImpl;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        UserResponse userResponse = userService.login(email, request, response);

        // then
        assertThat(response.getHeader("Authorization")).isNotNull();
        assertThat(response.getHeader("RefreshToken")).isNotNull();
        assertThat(userResponse.getResponseCode()).isEqualTo("200_OK");
    }

    @Test
    void 회원이_아닐_경우_201_CREATED_를_반환한다() {
        // given
        String email = "test1@test.com";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

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

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        userService.signUp(userRequest, request, response);

        // then
        assertThat(response.getHeader("Authorization")).isNotNull();
        assertThat(response.getHeader("RefreshToken")).isNotNull();
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

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

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

        request.addHeader("Authorization",
                Objects.requireNonNull(response.getHeader("Authorization")).substring(7));
        request.addHeader("RefreshToken",
                Objects.requireNonNull(response.getHeader("RefreshToken")).substring(7));

        // when
        userService.logout(request);

        // then
        String token = Objects.requireNonNull(request.getHeader("Authorization"));

        fakeRedisService.isTokenInBlacklist(token);
    }

    @Test
    void 내_정보를_업데이트_할_수_있다() {
        // given
        MultipartFile file = mock(MultipartFile.class);
        String email = "test@test.com";

        UserUpdate userUpdate = UserUpdate.builder()
                .age(24)
                .birth("20010103")
                .nickname("TEST_USER")
                .file(file)
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        userService.login(email, request, response);

        request.addHeader("Authorization",
                Objects.requireNonNull(response.getHeader("Authorization")).substring(7));
        request.addHeader("RefreshToken",
                Objects.requireNonNull(response.getHeader("RefreshToken")).substring(7));

        // when
        userService.update(userUpdate, request);

        // then
        User user = fakeUserRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundException("일치하는 유저 없음.", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        assertThat(user.getNickname()).isEqualTo(userUpdate.getNickname());
        assertThat(user.getAge()).isEqualTo(userUpdate.getAge());
        assertThat(user.getBirth()).isEqualTo(LocalDate.parse(userUpdate.getBirth(), formatter));
    }

    @Test
    void 내_정보를_확인_할_수_있다() {
        // given
        String email = "test@test.com";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        userService.login(email, request, response);

        request.addHeader("Authorization",
                Objects.requireNonNull(response.getHeader("Authorization")).substring(7));
        request.addHeader("RefreshToken",
                Objects.requireNonNull(response.getHeader("RefreshToken")).substring(7));

        User user = fakeUserRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundException("일치하는 유저 없음.", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        // when
        UserMyPage userMyPage = userService.viewMyPage(request);

        // then
        assertThat(userMyPage.getNickname()).isEqualTo(user.getNickname());
        assertThat(userMyPage.getEmail()).isEqualTo(user.getEmail());
        assertThat(userMyPage.getBirth()).isEqualTo(user.getBirth());
        assertThat(userMyPage.getAge()).isEqualTo(user.getAge());
    }

    @Test
    void 회원_탈퇴를_할_수_있다() {
        // given
        String email = "test@test.com";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        userService.login(email, request, response);

        request.addHeader("Authorization",
                Objects.requireNonNull(response.getHeader("Authorization")).substring(7));
        request.addHeader("RefreshToken",
                Objects.requireNonNull(response.getHeader("RefreshToken")).substring(7));

        // when
        userService.withDrawlMembership(request);

        // then
        User user = fakeUserRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundException("일치하는 유저 없음.", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        assertThat(user.isDeleted()).isTrue();
        assertThat(fakeRedisService.getValues(request.getHeader("RefreshToken"))).isNull();
        assertThatThrownBy(() -> {
            fakeRedisService.isTokenInBlacklist(request.getHeader("Authorization"));
        }).isInstanceOf(MalformedJwtException.class)
                .hasMessage("Invalid JWT token");
    }

    @Test
    void 일주일_내로_회원_탈퇴_요청을_취소_할_수_있다() {
        // given
        String email = "test@test.com";

        User user = fakeUserRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundException("일치하는 유저 없음.", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        user.delete(true);
        fakeUserRepository.save(user);

        // when
        userService.cancelWithdrawal(email, true);

        // then
        User userResult = fakeUserRepository.findByEmail(email).orElseThrow();

        assertThat(userResult.isDeleted()).isFalse();
    }
}
