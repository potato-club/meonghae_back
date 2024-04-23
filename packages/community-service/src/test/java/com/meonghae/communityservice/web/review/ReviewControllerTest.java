package com.meonghae.communityservice.web.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.application.port.S3ServicePort;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.application.review.ReviewService;
import com.meonghae.communityservice.dto.review.ReviewListDto;
import com.meonghae.communityservice.dto.review.ReviewReactionType;
import com.meonghae.communityservice.dto.s3.S3RequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        @Sql(value = "/sql/init-review.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-review.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewService reviewService;

    @MockBean
    private UserServicePort userService;

    @MockBean
    private S3ServicePort s3Service;

    @MockBean
    private RedisPort redisService;

    private final String token = "tester";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        when(redisService.getProfileImage(anyString()))
                .thenReturn(null);

        when(userService.getUserEmail(token))
                .thenReturn(token + "@example.com");

        when(userService.getUserEmail("test1"))
                .thenReturn("test1@example.com");
    }

    @Test
    public void 리뷰_타입으로_리뷰들을_조회한다() throws Exception {
        //given
        int type = 1;

        //when
        //then
        mockMvc.perform(
                get("/reviews/" + type)
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.numberOfElements").value(3));
    }

    @Test
    public void 리뷰_타입으로_사진이_있는_리뷰만_조회한다() throws Exception {
        //given
        int type = 1;

        //when
        //then
        mockMvc.perform(
                        get("/reviews/" + type)
                                .header("Authorization", token)
                                .param("photo", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.numberOfElements").value(2));
    }

    @Test
    public void 리뷰를_생성할_수_있다() throws Exception {
        //given
        String title = "create title";
        String content = "create content";
        String rate = "10";

        //when
        //then
        mockMvc.perform(
                post("/reviews")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("Authorization", token)
                        .param("type", "1")
                        .param("title", title)
                        .param("content", content)
                        .param("rating", rate))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("리뷰 등록 완료"));

        Slice<ReviewListDto> slice = reviewService.getReviewByType(1, token, 1,
                null, "RATING_DESC", false);

        assertThat(slice.getNumberOfElements()).isEqualTo(4);
        assertThat(slice.getContent().get(0).getTitle()).isEqualTo(title);
    }

    @Test
    public void 리뷰에_좋아요_또는_싫어요를_남길_수_있다() throws Exception {
        //given
        ReviewReactionType like = ReviewReactionType.builder()
                .isLike(true)
                .build();

        String typeDto = objectMapper.writeValueAsString(like);

        ReviewReactionType dislike = ReviewReactionType.builder()
                .isLike(false)
                .build();

        String typeDto2 = objectMapper.writeValueAsString(dislike);

        //when
        //then
        mockMvc.perform(
                post("/reviews/1/recommend")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(typeDto))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("추천 완료"));

        Slice<ReviewListDto> slice = reviewService.getReviewByType(1, token, 1,
                null, "RATING_DESC", false);

        assertThat(slice.getContent().get(0).getLikes()).isEqualTo(4);
        assertThat(slice.getContent().get(0).getDislikes()).isEqualTo(0);

        mockMvc.perform(
                        post("/reviews/1/recommend")
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(typeDto2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("비추 완료"));

        Slice<ReviewListDto> slice2 = reviewService.getReviewByType(1, token, 1,
                null, "RATING_DESC", false);

        assertThat(slice2.getContent().get(0).getLikes()).isEqualTo(3);
        assertThat(slice2.getContent().get(0).getDislikes()).isEqualTo(1);
    }

    @Test
    public void 리뷰_삭제() throws Exception {
        //given
        String my_token = "test1";

        //when
        //then
        mockMvc.perform(
                delete("/reviews/1")
                        .header("Authorization", my_token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("삭제 완료"));

        Slice<ReviewListDto> slice = reviewService.getReviewByType(1, token, 1,
                null, "LATEST", false);

        assertThat(slice.getNumberOfElements()).isEqualTo(2);
    }
}