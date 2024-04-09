package com.meonghae.userservice.controller;

import com.meonghae.userservice.mock.FakePetServiceClient;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockBean(FakePetServiceClient.class)
    private FakePetServiceClient fakePetServiceClient;

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
}
