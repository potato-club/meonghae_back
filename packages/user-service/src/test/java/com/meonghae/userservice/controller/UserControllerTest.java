package com.meonghae.userservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void 사용자는_로그인을_진행할_수_있다() throws Exception {
        // given
        String email = "test@test.com";

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/login")
                                .param("email", email)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.responseCode").value("200_OK"));
    }
}
