package com.meonghae.communityservice.unit.application.review;

import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.application.port.S3ServicePort;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.application.review.ReviewReactionService;
import com.meonghae.communityservice.application.review.ReviewService;
import com.meonghae.communityservice.application.review.port.ReviewRepository;
import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.domain.review.ReviewCatalog;
import com.meonghae.communityservice.dto.review.ReviewList;
import com.meonghae.communityservice.dto.review.ReviewRequest;
import com.meonghae.communityservice.exception.custom.ReviewException;
import com.meonghae.communityservice.exception.error.ErrorCode;
import com.meonghae.communityservice.mock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReviewServiceTestWithMockito {

    private ReviewService reviewService;

    private ReviewReactionService reactionService = mock(ReviewReactionService.class);

    private ReviewRepository reviewRepository = mock(FakeReviewRepo.class);

    private S3ServicePort s3Service = mock(FakeS3Service.class);

    private UserServicePort userService = mock(FakeUserService.class);

    private RedisPort redisPort = mock(FakeRedis.class);

    private final MockMultipartFile multipartFile = new MockMultipartFile(
            "testFile",
            "originTestFile",
            "testType",
            "content".getBytes()
    );

    @BeforeEach
    void init() {
        this.reviewService = ReviewService.builder()
                .reviewRepository(reviewRepository)
                .redisService(redisPort)
                .s3Service(s3Service)
                .reactionService(reactionService)
                .userService(userService)
                .build();
    }

    @Test
    public void 리뷰_타입으로_리뷰_리스트를_조회할_수_있다() throws Exception {
        //given
        String token = "token";
        Review review1 = createReview("title1", "content1");
        Review review2 = createReviewWithPhoto("title2", "content2");

        Slice<Review> mockSlice = new PageImpl<>(List.of(review1, review2));

        when(reviewRepository.findByCatalogAndKeywordAndSortType(any(PageRequest.class), any(ReviewCatalog.class),
                nullable(String.class))).thenReturn(mockSlice);

        when(reactionService.getReviewReactions(anyList(), eq(token)))
                .thenReturn(new HashMap<>());

        when(userService.getUserEmail(token))
                .thenReturn("tester");

        when(redisPort.getNickname("tester"))
                .thenReturn("testNickname");

        when(redisPort.getProfileImage("tester"))
                .thenReturn("test.jpg");

        //when
        Slice<ReviewList> reviewList = reviewService
                .getReviewByType(1, token, 1, null, "LATEST", false);

        //then
        assertThat(reviewList.getNumberOfElements()).isEqualTo(2);
        assertThat(reviewList.getContent().get(0).getTitle()).isEqualTo("title1");
        assertThat(reviewList.getContent().get(0).getContent()).isEqualTo("content1");
        assertThat(reviewList.getContent().get(1).getTitle()).isEqualTo("title2");
        assertThat(reviewList.getContent().get(1).getContent()).isEqualTo("content2");
    }

    @Test
    public void 리뷰_타입으로_사진만_있는_리뷰_리스트만을_조회할_수_있다() throws Exception {
        //given
        String token = "token";
        Review review1 = createReview("title1", "content1");
        Review review2 = createReviewWithPhoto("title2", "content2");

        Slice<Review> mockSlice = new PageImpl<>(List.of(review2));

        when(reviewRepository.findByCatalogAndHasImageAndKeywordAndSortType(any(PageRequest.class),
                any(ReviewCatalog.class), nullable(String.class))).thenReturn(mockSlice);

        when(reactionService.getReviewReactions(anyList(), eq(token)))
                .thenReturn(new HashMap<>());

        when(userService.getUserEmail(token))
                .thenReturn("tester");

        when(redisPort.getNickname("tester"))
                .thenReturn("testNickname");

        when(redisPort.getProfileImage("tester"))
                .thenReturn("test.jpg");

        //when
        Slice<ReviewList> reviewList = reviewService.getReviewByType(1, token, 1, null, "LATEST", true);

        //then
        assertThat(reviewList.getNumberOfElements()).isEqualTo(1);
        assertThat(reviewList.getContent().get(0).getTitle()).isEqualTo("title2");
        assertThat(reviewList.getContent().get(0).getContent()).isEqualTo("content2");
    }

    @Test
    public void 잘못된_카탈로그_키_값_입력시_예외가_던져진다() throws Exception {
        //given
        int key = 88;

        //when
        //then
        assertThatThrownBy(() ->
                reviewService.getReviewByType(key, "token", 1, null, null, true))
                .isInstanceOf(ReviewException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("errorMessage", "잘못된 Catalog Type 입니다.");
    }

    private Review createReview(String title, String content) {
        String token = "tester";
        ReviewRequest request = ReviewRequest.builder()
                .title(title)
                .content(content)
                .rating(10)
                .build();

        return Review.create(request, ReviewCatalog.COLLAR, token);
    }

    private Review createReviewWithPhoto(String title, String content) {
        List<MultipartFile> imageList = new ArrayList<>();
        imageList.add(multipartFile);

        String token = "tester";
        ReviewRequest request = ReviewRequest.builder()
                .title(title)
                .content(content)
                .rating(10)
                .images(imageList)
                .build();

        return Review.create(request, ReviewCatalog.COLLAR, token);
    }
}
