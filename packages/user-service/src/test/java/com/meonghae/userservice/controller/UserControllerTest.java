package com.meonghae.userservice.controller;

import com.meonghae.userservice.dto.user.UserRequest;
import com.meonghae.userservice.mock.FakePetServiceClient;
import com.meonghae.userservice.mock.FakeS3ServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestPropertySource(properties = {"spring.config.location = classpath:application-test-user.yml"})
@SqlGroup({
        @Sql(value = "/sql/controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FakePetServiceClient fakePetServiceClient;

    @MockBean
    private FakeS3ServiceClient fakeS3ServiceClient;

    @Test
    void 사용자는_로그인을_진행할_수_있다() throws Exception {
        // given
        String email = "test@test.com";
        String androidId = "test-android-123-456-789";
        String fcmToken = "test-FCMToken-123-456-789";

        // when
        // then
        mockMvc.perform(
                        get("/login")
                                .queryParam("email", email)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("androidId", androidId)
                                .header("FCMToken", fcmToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").isEmpty())
                .andExpect(jsonPath("$.responseCode").value("200_OK"))
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("RefreshToken"));
    }

    @Test
    void 회원이_아닐_경우_회원_가입을_진행할_수_있다() throws Exception {
        // given
        String email = "test1@test.com";
        String androidId = "test-android-123-456-780";
        String fcmToken = "test-FCMToken-123-456-780";

        UserRequest request = UserRequest.builder()
                .email(email)
                .nickname("Test-User")
                .age(25)
                .birth("20000101")
                .build();

        // when
        // then
        mockMvc.perform(
                    get("/login")
                         .queryParam("email", email)
                         .contentType(MediaType.APPLICATION_JSON)
                         .header("androidId", androidId)
                         .header("FCMToken", fcmToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.responseCode").value("201_CREATED"));

        mockMvc.perform(
                    post("/signup")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
                        .param("nickname", request.getNickname())
                        .param("age", String.valueOf(25))
                        .param("birth", "20000101")
                        .header("androidId", androidId)
                        .header("FCMToken", fcmToken)
            )
            .andExpect(status().isOk())
            .andExpect(header().exists("Authorization"))
            .andExpect(header().exists("RefreshToken"));
    }

    @Test
    void 내_정보를_확인할_수_있다() throws Exception {
        // given
        String email = "test@test.com";
        String androidId = "test-android-123-456-789";
        String fcmToken = "test-FCMToken-123-456-789";

        UserRequest request = UserRequest.builder()
                .email(email)
                .nickname("Test-User")
                .age(25)
                .birth("20000101")
                .build();

        // when
        // then
        mockMvc.perform(
                        get("/login")
                                .queryParam("email", email)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("androidId", androidId)
                                .header("FCMToken", fcmToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("200_OK"))
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("RefreshToken"))
                .andDo(result -> {
                    String accessToken = Objects.requireNonNull(result.getResponse().getHeader("Authorization")).substring(7);
                    String refreshToken = Objects.requireNonNull(result.getResponse().getHeader("RefreshToken")).substring(7);

                    mockMvc.perform(
                                    get("/mypage")
                                            .header("Authorization", accessToken)
                                            .header("RefreshToken", refreshToken)
                            )
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.email").value(email))
                            .andExpect(jsonPath("$.nickname").value(request.getNickname()))
                            .andExpect(jsonPath("$.age").value(request.getAge()))
                            .andExpect(jsonPath("$.birth").value(request.getBirth()));
                });
    }
}
