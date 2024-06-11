package com.meonghae.communityservice.web.board.board;

import com.jayway.jsonpath.JsonPath;
import com.meonghae.communityservice.application.board.BoardService;
import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.application.port.S3ServicePort;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.dto.board.BoardDetailDto;
import com.meonghae.communityservice.dto.s3.S3RequestDto;
import com.meonghae.communityservice.exception.custom.BoardException;
import com.meonghae.communityservice.exception.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@SqlGroup({
        @Sql(value = "/sql/init-board.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-board.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
})
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @MockBean
    private RedisPort redisService;

    @MockBean
    private S3ServicePort s3Service;

    @MockBean
    private UserServicePort userService;

    private final MockMultipartFile multipartFile_1 = new MockMultipartFile(
            "testFile_1",
            "originTestFile_1",
            "testType",
            "content".getBytes()
    );

    private final MockMultipartFile multipartFile_2 = new MockMultipartFile(
            "testFile_2",
            "originTestFile_2",
            "testType",
            "content".getBytes()
    );

    private final String token = "test token";

    @BeforeEach
    void init() {
        when(userService.getUserEmail(token))
                .thenReturn("tester@example.com");

        when(userService.getUserEmail("owner1"))
                .thenReturn("test1@example.com");

        when(userService.getUserEmail("owner2"))
                .thenReturn("test2@example.com");

        when(userService.getUserEmail("owner3"))
                .thenReturn("test3@example.com");
    }

    @Test
    public void 게시글_타입으로_게시글_리스트를_조회할_수_있다() throws Exception {
        //given
        String type = "1";
        String page = "1";

        //when
        //then
        mockMvc.perform(
                get("/boards")
                        .queryParam("type", type)
                        .queryParam("page", page))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.numberOfElements").value(2));
    }

    @Test
    public void 게시글_id_로_특정_게시글을_조회할_수_있다() throws Exception {
        //given
        Long id = 1L;

        //when
        //then
        mockMvc.perform(
                get("/boards/1")
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Title 1"))
                .andExpect(jsonPath("$.content").value("Test Content 1"))
                .andExpect(jsonPath("$.likeStatus").value(true));
    }

    @Test
    public void 메인에_띄울_인기_게시글을_조회할_수_있다() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(
                get("/boards/main"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Title 1"))
                .andExpect(jsonPath("$[0].type").value("SHOW"))
                .andExpect(jsonPath("$[0].commentSize").value(4));
    }

    @Test
    public void 게시글을_생성할_수_있다() throws Exception {
        //given
        String title = "new title";
        String content = "new content";

        //when
        //then
        mockMvc.perform(
                multipart("/boards/1")
                        .file(multipartFile_1)
                        .file(multipartFile_2)
                        .param("title", title)
                        .param("content", content)
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("게시글 생성 완료"));

        BoardDetailDto board = boardService.getBoard(4L, token);
        assertThat(board.getTitle()).isEqualTo(title);
        assertThat(board.getContent()).isEqualTo(content);
    }

    @Test
    public void 게시글에_좋아요를_남길_수_있다() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(
                post("/boards/2/like")
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("추천 완료"));

        BoardDetailDto board = boardService.getBoard(2L, token);
        assertThat(board.getLikes()).isEqualTo(1);
    }

    @Test
    public void 기존_게시글을_수정할_수_있다() throws Exception {
        //given
        String owner_token = "owner1";
        String updateTitle= "update title";
        String updateContent = "update content";

        MvcResult result = mockMvc.perform(
                        get("/boards/1")
                                .header("Authorization", token))
                .andReturn();

        //when
        //then
        mockMvc.perform(
                put("/boards/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", updateTitle)
                        .param("content", updateContent)
                        .header("Authorization", owner_token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("수정 완료"));

        BoardDetailDto board = boardService.getBoard(1L, token);

        assertThat(board.getTitle()).isEqualTo(updateTitle);
        assertThat(board.getContent()).isEqualTo(updateContent);
    }

    @Test
    public void 게시글을_삭제할_수_있다() throws Exception {
        //given
        String owner_token= "owner1";

        //when
        //then
        mockMvc.perform(
                delete("/boards/1")
                        .header("Authorization", owner_token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("삭제 완료"));

        assertThatThrownBy(() -> boardService.getBoard(1L, "token"))
                .isInstanceOf(BoardException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("errorMessage", "board is not exist");
    }
}