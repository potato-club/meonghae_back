package com.meonghae.userservice.core;

import com.meonghae.userservice.core.jwt.JwtTokenProvider;
import com.meonghae.userservice.domin.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtFilterTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 로그인_성공_시_AccessToken_을_발급한다() {
        // given
        String email = "test@test.com";
        UserRole userRole = UserRole.USER;
        String androidId = "hi-test-1234";

        // when
        String accessToken = jwtTokenProvider.createAccessToken(email, userRole, androidId);

        // then
        assertThat(jwtTokenProvider.validateToken(accessToken)).isFalse();
    }

    @Test
    void 로그인_성공_시_RefreshToken_을_발급한다() {
        // given
        String email = "test@test.com";
        UserRole userRole = UserRole.USER;
        String androidId = "hi-test-1234";

        // when
        String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole, androidId);

        // then
        assertThat(jwtTokenProvider.validateToken(refreshToken)).isFalse();
    }

    @Test
    void 로그_아웃_시_Token_을_만료시킨다() {
        // given

        // when

        // then
    }

    @Test
    void 로그_아웃_시_AccessToken_을_블랙리스트에_등록한다() {
        // given

        // when

        // then
    }

    @Test
    void Token_이_양호한지_검사한다() {
        // given

        // when

        // then
    }

    @Test
    void RefreshToken_으로_AccessToken_을_재발급한다() {
        // given

        // when

        // then
    }

    @Test
    void Token_에서_Email_을_얻을_수_있다() {
        // given

        // when

        // then
    }
}
