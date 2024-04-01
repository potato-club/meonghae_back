package com.meonghae.communityservice.unit.application.review;

import com.meonghae.communityservice.application.review.ReviewReactionService;
import com.meonghae.communityservice.application.review.ReviewService;
import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.domain.review.ReviewCatalog;
import com.meonghae.communityservice.dto.review.ReviewRequest;
import com.meonghae.communityservice.exception.custom.ReviewException;
import com.meonghae.communityservice.mock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    private ReviewService reviewService;
    private final MockMultipartFile multipartFile = new MockMultipartFile(
            "testFile",
            "originTestFile",
            "testType",
            "content".getBytes()
    );

    @BeforeEach
    void init() {
        FakeUserService fakeUserService = new FakeUserService();
        FakeReviewRepo fakeReviewRepo = new FakeReviewRepo();

        ReviewReactionService reactionService = ReviewReactionService.builder()
                .userService(fakeUserService)
                .reviewRepository(fakeReviewRepo)
                .reactionRepository(new FakeReactionRepo())
                .build();

        this.reviewService = ReviewService.builder()
                .userService(fakeUserService)
                .reviewRepository(fakeReviewRepo)
                .s3Service(new FakeS3Service())
                .reactionService(reactionService)
                .redisService(new FakeRedis())
                .build();
    }

    @Test
    public void 리뷰를_작성할_수_있다() throws Exception {
        //given
        String token = "tester";
        ReviewRequest request = ReviewRequest.builder()
                .title("test title")
                .content("test content")
                .rating(10)
                .build();

        //when
        Review review = reviewService.createReview(1, request, token);

        //then
        assertThat(review.getEmail()).isEqualTo(token + "@test.com");
        assertThat(review.getRating()).isEqualTo(10);
        assertThat(review.getCatalog()).isEqualByComparingTo(ReviewCatalog.COLLAR);
        assertThat(review.getHasImage()).isFalse();
    }

    @Test
    public void 사진과_함께_리뷰를_작성할_수_있다() throws Exception {
        //given
        List<MultipartFile> imageList = new ArrayList<>();
        imageList.add(multipartFile);

        String token = "tester";
        ReviewRequest request = ReviewRequest.builder()
                .title("test title")
                .content("test content")
                .rating(10)
                .images(imageList)
                .build();

        //when
        Review review = reviewService.createReview(1, request, token);

        //then
        assertThat(review.getEmail()).isEqualTo(token + "@test.com");
        assertThat(review.getRating()).isEqualTo(10);
        assertThat(review.getCatalog()).isEqualByComparingTo(ReviewCatalog.COLLAR);
        assertThat(review.getHasImage()).isTrue();
    }

    @Test
    public void 잘못된_카탈로그로_리뷰_작성_시_예외가_던져진다() throws Exception {
        //given
        String token = "tester";
        ReviewRequest request = ReviewRequest.builder()
                .title("test title")
                .content("test content")
                .rating(10)
                .build();

        //when
        //then
        assertThatThrownBy(() -> reviewService.createReview(100, request, token))
                .isInstanceOf(ReviewException.class)
                .hasFieldOrPropertyWithValue("errorMessage", "잘못된 Catalog Type 입니다.");
    }

    @Test
    public void 세장보다_많은_수의_이미지로_리뷰_작성시_예외가_던져진다() throws Exception {
        //given
        List<MultipartFile> imageList = new ArrayList<>();
        imageList.add(multipartFile);
        imageList.add(multipartFile);
        imageList.add(multipartFile);
        imageList.add(multipartFile);

        String token = "tester";
        ReviewRequest request = ReviewRequest.builder()
                .title("test title")
                .content("test content")
                .rating(10)
                .images(imageList)
                .build();

        //when
        //then
        assertThatThrownBy(() -> reviewService.createReview(1, request, token))
                .isInstanceOf(ReviewException.class)
                .hasFieldOrPropertyWithValue("errorMessage", "리뷰 사진은 3개까지 업로드 가능합니다.");
    }

    @Test
    public void 리뷰_작성자가_아닌_사람이_리뷰_삭제_시_예외가_던져진다() throws Exception {
        //given
        String token = "tester";
        ReviewRequest request = ReviewRequest.builder()
                .title("test title")
                .content("test content")
                .rating(10)
                .build();

        Review review = reviewService.createReview(1, request, token);

        //when
        //then
        String token2 = "test";
        assertThatThrownBy(() -> reviewService.deleteReview(review.getId(), token2))
                .isInstanceOf(ReviewException.class)
                .hasFieldOrPropertyWithValue("errorMessage", "리뷰 작성자만 삭제 가능합니다.");
    }
}