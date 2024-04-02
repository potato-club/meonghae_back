package com.meonghae.communityservice.unit.application.review;

import com.meonghae.communityservice.application.review.ReviewReactionService;
import com.meonghae.communityservice.application.review.ReviewService;
import com.meonghae.communityservice.domain.review.RecommendStatus;
import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.dto.review.ReviewReactionType;
import com.meonghae.communityservice.dto.review.ReviewRequest;
import com.meonghae.communityservice.mock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReviewReactionServiceTest {

    private ReviewService reviewService;
    private ReviewReactionService reactionService;

    @BeforeEach
    void init() {
        FakeUserService fakeUserService = new FakeUserService();
        FakeReviewRepo fakeReviewRepo = new FakeReviewRepo();

        this.reactionService = ReviewReactionService.builder()
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
    public void 리뷰에_추천을_할_수_있다() throws Exception {
        //given
        Review review = createReview();

        String token = "tester";

        //when
        String res = reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(true)
                .build());

        //then
        assertThat(res).isEqualTo("추천 완료");
        assertThat(review.getLikes()).isEqualTo(1);
        assertThat(review.getDislikes()).isZero();
    }

    @Test
    public void 추천을_두_번_누르면_추천이_취소된다() throws Exception {
        //given
        Review review = createReview();

        String token = "tester";

        reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(true)
                .build());

        //when
        String res = reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(true)
                .build());

        //then
        assertThat(res).isEqualTo("추천 취소");
        assertThat(review.getLikes()).isZero();
        assertThat(review.getDislikes()).isZero();
    }

    @Test
    public void 추천상태인_리뷰에_비추천을_할_수_있다() throws Exception {
        //given
        Review review = createReview();

        String token = "tester";

        reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(true)
                .build());

        //when
        String res = reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(false)
                .build());

        //then
        assertThat(res).isEqualTo("비추 완료");
        assertThat(review.getLikes()).isZero();
        assertThat(review.getDislikes()).isEqualTo(1);
    }

    @Test
    public void 리뷰에_비추천을_할_수_있다() throws Exception {
        //given
        Review review = createReview();

        String token = "tester";

        //when
        String res = reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(false)
                .build());

        //then
        assertThat(res).isEqualTo("비추 완료");
        assertThat(review.getLikes()).isZero();
        assertThat(review.getDislikes()).isEqualTo(1);
    }

    @Test
    public void 비추천을_두_번_누르면_비추천이_취소된다() throws Exception {
        //given
        Review review = createReview();

        String token = "tester";

        reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(false)
                .build());

        //when
        String res = reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(false)
                .build());

        //then
        assertThat(res).isEqualTo("비추 취소");
        assertThat(review.getLikes()).isZero();
        assertThat(review.getDislikes()).isZero();
    }

    @Test
    public void 비추천상태인_리뷰에_추천을_할_수_있다() throws Exception {
        //given
        Review review = createReview();

        String token = "tester";

        reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(false)
                .build());

        //when
        String res = reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(true)
                .build());

        //then
        assertThat(res).isEqualTo("추천 완료");
        assertThat(review.getLikes()).isEqualTo(1);
        assertThat(review.getDislikes()).isZero();
    }

    @Test
    public void 유저가_추천이나_비추천한_리뷰_정보를_가져온다() throws Exception {
        //given
        Review review = createReview();
        Review review_2 = createReview();
        Review review_3 = createReview();

        String token = "tester";

        reactionService.toggleRecommendedReview(review.getId(), token, ReviewReactionType.builder()
                .isLike(true)
                .build());

        reactionService.toggleRecommendedReview(review_2.getId(), token, ReviewReactionType.builder()
                .isLike(false)
                .build());

        reactionService.toggleRecommendedReview(review_3.getId(), token, ReviewReactionType.builder()
                .isLike(true)
                .build());

        reactionService.toggleRecommendedReview(review_3.getId(), token, ReviewReactionType.builder()
                .isLike(true)
                .build());

        //when
        Map<Long, RecommendStatus> reactions =
                reactionService.getReviewReactions(List.of(review.getId(), review_2.getId(), review_3.getId()), token);

        //then
        assertThat(reactions.keySet()).hasSize(3);
        assertThat(reactions.get(review.getId())).isEqualByComparingTo(RecommendStatus.TRUE);
        assertThat(reactions.get(review_2.getId())).isEqualByComparingTo(RecommendStatus.FALSE);
        assertThat(reactions.get(review_3.getId())).isEqualByComparingTo(RecommendStatus.NONE);
    }

    Review createReview() {
        String token = "creator";
        ReviewRequest request = ReviewRequest.builder()
                .title("test title")
                .content("test content")
                .rating(10)
                .build();

        return reviewService.createReview(1, request, token);
    }
}