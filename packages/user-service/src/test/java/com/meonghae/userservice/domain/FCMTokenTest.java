package com.meonghae.userservice.domain;

import com.meonghae.userservice.domin.FCMToken.FCMToken;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FCMTokenTest {

    @Test
    void 첫_로그인_시_FCMToken을_생성할_수_있다() {
        // given
        FCMToken fcmToken = FCMToken.builder()
                .email("test@test.com")
                .token("fveXjrqISn260Etx53JUxJ:APA19bGrSuFe7BBjd1N7XW8AL-i_3YUrDGKQG_dRD-gOal-_PO1C621uyNCuA7wypTtSE_5NhTEQY3YdyUt2RO4FcQiVSNqUHrrRT2721PHCl-QIX6qNfxjEI4rG_KpjsZRfzm7PPWNl")
                .build();

        // when
        // then
        assertThat(fcmToken.getId()).isNull();
        assertThat(fcmToken.getEmail()).isEqualTo("test@test.com");
        assertThat(fcmToken.getToken()).isEqualTo("fveXjrqISn260Etx53JUxJ:APA19bGrSuFe7BBjd1N7XW8AL-i_3YUrDGKQG_dRD-gOal-_PO1C621uyNCuA7wypTtSE_5NhTEQY3YdyUt2RO4FcQiVSNqUHrrRT2721PHCl-QIX6qNfxjEI4rG_KpjsZRfzm7PPWNl");
    }

    @Test
    void 로그인_시_FCMToken을_업데이트_할_수_있다() {
        // given
        FCMToken fcmToken = FCMToken.builder()
                .id(1L)
                .email("test@test.com")
                .token("fveXjrqISn260Etx53JUxJ:APA19bGrSuFe7BBjd1N7XW8AL-i_3YUrDGKQG_dRD-gOal-_PO1C621uyNCuA7wypTtSE_5NhTEQY3YdyUt2RO4FcQiVSNqUHrrRT2721PHCl-QIX6qNfxjEI4rG_KpjsZRfzm7PPWNl")
                .build();

        String updateToken = "vfeXjrqISn260Etx53JUxJ:APA19bGrSuFe7BBjd1N7XW8AL-i_3YUrDGKQG_dRD-gOal-_PO1C621uyNCuA7wypTtSE_5NhTEQY3YdyUt2RO4FcQiVSNqUHrrRT2721PHCl-QIX6qNfxjEI4rG_KpjsZRfzm7PPWNl";

        // when
        FCMToken token = fcmToken.update(updateToken);

        // then
        assertThat(token.getId()).isEqualTo(1L);
        assertThat(token.getEmail()).isEqualTo("test@test.com");
        assertThat(token.getToken()).isEqualTo("vfeXjrqISn260Etx53JUxJ:APA19bGrSuFe7BBjd1N7XW8AL-i_3YUrDGKQG_dRD-gOal-_PO1C621uyNCuA7wypTtSE_5NhTEQY3YdyUt2RO4FcQiVSNqUHrrRT2721PHCl-QIX6qNfxjEI4rG_KpjsZRfzm7PPWNl");
    }
}
