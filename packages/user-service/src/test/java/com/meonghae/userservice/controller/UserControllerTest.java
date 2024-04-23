package com.meonghae.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meonghae.userservice.core.exception.ErrorCode;
import com.meonghae.userservice.core.exception.impl.NotFoundException;
import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.dto.user.UserCancel;
import com.meonghae.userservice.dto.user.UserRequest;
import com.meonghae.userservice.dto.user.UserUpdate;
import com.meonghae.userservice.service.client.feign.PetServiceClient;
import com.meonghae.userservice.service.client.feign.S3ServiceClient;
import com.meonghae.userservice.service.port.RedisService;
import com.meonghae.userservice.service.port.UserRepository;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.sql.DataSource;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestPropertySource(properties = {"spring.config.location = classpath:application-test-user.yml"})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetServiceClient petServiceClient;

    @MockBean
    private S3ServiceClient s3ServiceClient;

    @BeforeAll
    public static void setUp(@Autowired DataSource dataSource) {
        // 스프링 컨텍스트가 로드된 후, 한 번만 실행되는 초기화 블록
        ResourceDatabasePopulator pt = new ResourceDatabasePopulator();
        pt.addScript(new ClassPathResource("/sql/controller-test-data.sql"));
        pt.execute(dataSource);
    }

    @AfterAll
    public static void cleanUp(@Autowired DataSource dataSource) {
        // 모든 테스트가 완료된 후, 데이터 정리
        ResourceDatabasePopulator pt = new ResourceDatabasePopulator();
        pt.addScript(new ClassPathResource("/sql/delete-all-data.sql"));
        pt.execute(dataSource);
    }

    @Test
    void 회원이_아닐_경우_회원_가입을_진행할_수_있다() throws Exception {
        // given
        String email = "test1@test.com";
        String androidId = "test-android-123-456-780";
        String fcmToken = "test-FCMToken-123-456-780";

        UserRequest userRequest = UserRequest.builder()
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.responseCode").value("201_CREATED"));

        mockMvc.perform(
                        post("/signup")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("email", email)
                                .param("nickname", userRequest.getNickname())
                                .param("age", String.valueOf(userRequest.getAge()))
                                .param("birth", userRequest.getBirth())
                                .header("androidId", androidId)
                                .header("FCMToken", fcmToken)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("RefreshToken"));
    }

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
                                .param("email", email)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("androidId", androidId)
                                .header("FCMToken", fcmToken)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").isEmpty())
                .andExpect(jsonPath("$.responseCode").value("200_OK"))
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("RefreshToken"));
    }

    @Test
    void 내_정보를_확인할_수_있다() throws Exception {
        // given
        String email = "test@test.com";
        String androidId = "test-android-123-456-789";
        String fcmToken = "test-FCMToken-123-456-789";

        ResultActions resultActions = mockMvc.perform(get("/login")
                                        .queryParam("email", email)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("androidId", androidId)
                                        .header("FCMToken", fcmToken));

        // when
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("200_OK"))
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("RefreshToken"))
                .andReturn();

        String accessToken = Objects.requireNonNull(mvcResult.getResponse().getHeader("Authorization")).substring(7);
        String refreshToken = Objects.requireNonNull(mvcResult.getResponse().getHeader("RefreshToken")).substring(7);

        // then
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundException("Not Found User", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        mockMvc.perform(
                        get("/mypage")
                                .header("Authorization", accessToken)
                                .header("RefreshToken", refreshToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.age").value(user.getAge()))
                .andExpect(jsonPath("$.birth").value(user.getBirth().toString()));
    }

    @Test
    void 내_정보를_수정할_수_있다() throws Exception {
        // given
        String email = "test@test.com";
        String androidId = "test-android-123-456-789";
        String fcmToken = "test-FCMToken-123-456-789";

        UserUpdate request = UserUpdate.builder()
                .nickname("Test-User-1")
                .age(25)
                .birth("20000202")
                .build();

        ResultActions resultActions = mockMvc.perform(get("/login")
                .queryParam("email", email)
                .contentType(MediaType.APPLICATION_JSON)
                .header("androidId", androidId)
                .header("FCMToken", fcmToken));

        // when
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("200_OK"))
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("RefreshToken"))
                .andReturn();

        String accessToken = Objects.requireNonNull(mvcResult.getResponse().getHeader("Authorization")).substring(7);
        String refreshToken = Objects.requireNonNull(mvcResult.getResponse().getHeader("RefreshToken")).substring(7);

        // then
        mockMvc.perform(
                        put("/mypage")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("nickname", request.getNickname())
                                .param("age", String.valueOf(request.getAge()))
                                .param("birth", request.getBirth())
                                .header("Authorization", accessToken)
                                .header("RefreshToken", refreshToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void 로그_아웃을_하면_토큰이_만료된다() throws Exception {
        // given
        String email = "test@test.com";
        String androidId = "test-android-123-456-789";
        String fcmToken = "test-FCMToken-123-456-789";

        ResultActions resultActions = mockMvc.perform(get("/login")
                .queryParam("email", email)
                .contentType(MediaType.APPLICATION_JSON)
                .header("androidId", androidId)
                .header("FCMToken", fcmToken));

        // when
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("200_OK"))
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("RefreshToken"))
                .andReturn();

        String accessToken = Objects.requireNonNull(mvcResult.getResponse().getHeader("Authorization")).substring(7);
        String refreshToken = Objects.requireNonNull(mvcResult.getResponse().getHeader("RefreshToken")).substring(7);

        // then
        mockMvc.perform(get("/logout")
                .header("Authorization", accessToken)
                .header("RefreshToken", refreshToken))
                .andExpect(status().isOk());

        assertThatThrownBy(() -> {
            redisService.isTokenInBlacklist(accessToken);
        }).isInstanceOf(MalformedJwtException.class)
                .hasMessage("Invalid JWT token");
    }

    @Test
    void 회원_탈퇴를_진행한_후_일주일_이내로_취소할_수_있다() throws Exception {
        // given
        String email = "test@test.com";
        String androidId = "test-android-123-456-789";
        String fcmToken = "test-FCMToken-123-456-789";

        UserCancel request = UserCancel.builder()
                .email("test@test.com")
                .agreement(true)
                .build();

        ResultActions resultActions = mockMvc.perform(get("/login")
                .queryParam("email", email)
                .contentType(MediaType.APPLICATION_JSON)
                .header("androidId", androidId)
                .header("FCMToken", fcmToken));

        // when
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("200_OK"))
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("RefreshToken"))
                .andReturn();

        String accessToken = Objects.requireNonNull(mvcResult.getResponse().getHeader("Authorization")).substring(7);
        String refreshToken = Objects.requireNonNull(mvcResult.getResponse().getHeader("RefreshToken")).substring(7);

        // then
        mockMvc.perform(put("/withdrawal")
                        .header("Authorization", accessToken)
                        .header("RefreshToken", refreshToken))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundException("Not Found User", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        assertThat(user.isDeleted()).isEqualTo(true);

        mockMvc.perform(put("/cancel")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> {
            throw new NotFoundException("Not Found User", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        assertThat(user.isDeleted()).isEqualTo(false);
    }
}
