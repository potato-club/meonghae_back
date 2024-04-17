package com.meonghae.communityservice.web.board.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meonghae.communityservice.application.board.BoardCommentService;
import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.dto.comment.CommentChild;
import com.meonghae.communityservice.dto.comment.CommentParent;
import com.meonghae.communityservice.dto.comment.CommentRequest;
import com.meonghae.communityservice.exception.custom.CommentException;
import com.meonghae.communityservice.exception.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@SqlGroup({
        @Sql(value = "/sql/init-board.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-board.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class BoardCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardCommentService commentService;

    @MockBean
    private UserServicePort userService;

    @MockBean
    private RedisPort redisService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String token = "tester";

    @BeforeEach
    void init() {
        when(redisService.getProfileImage(anyString()))
                .thenReturn(null);

        when(userService.getUserEmail(token))
                .thenReturn(token + "@example.com");
    }

    @Test
    public void 게시글_id_로_부모_댓글_조회() throws Exception {
        //given
        Long boardId = 1L;

        //when
        //then
        mockMvc.perform(
                get("/boardComments/" + boardId)
                        .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.numberOfElements").value(2));
    }

    @Test
    public void 부모_댓글_id_로_자식_댓글_조회() throws Exception {
        //given
        Long commentId = 2L;

        //when
        //then
        mockMvc.perform(
                        get("/boardComments/" + commentId + "/reply")
                                .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.numberOfElements").value(2));
    }

    @Test
    public void 부모_댓글_작성() throws Exception {
        //given
        CommentRequest dto = CommentRequest.builder()
                .comment("test hi 1")
                .build();

        String requestDto = objectMapper.writeValueAsString(dto);

        //when
        //then
        mockMvc.perform(
                post("/boardComments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDto)
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("댓글 생성 완료"));

        Slice<CommentParent> comments = commentService.getParentComments(1, 1L);
        assertThat(comments.getNumberOfElements()).isEqualTo(3);
    }

    @Test
    public void 자식_댓글_작성() throws Exception {
        //given
        CommentRequest dto = CommentRequest.builder()
                .comment("test hi 1")
                .build();

        String requestDto = objectMapper.writeValueAsString(dto);

        //when
        //then
        mockMvc.perform(
                        post("/boardComments/1/reply")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestDto)
                                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("대댓글 생성 완료"));

        Slice<CommentChild> comments = commentService.getChildComments(1, 1L);

        assertThat(comments.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void 댓글_수정() throws Exception {
        //given
        CommentRequest dto = CommentRequest.builder()
                .comment("update my comment")
                .build();

        String requestDto = objectMapper.writeValueAsString(dto);

        //when
        //then
        mockMvc.perform(
                        put("/boardComments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestDto)
                                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("댓글 수정 완료"));

        Slice<CommentParent> parents = commentService.getParentComments(1, 1L);
        assertThat(parents.getContent().get(1).getComment()).isEqualTo("update my comment");
        assertThat(parents.getContent().get(1).getUpdate()).isTrue();
    }

    @Test
    public void 댓글_삭제() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(
                delete("/boardComments/1")
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("댓글 삭제 완료"));

        assertThatThrownBy(() -> commentService.getChildComments(1, 1L))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("errorMessage", "this comment is not parent");
    }
}