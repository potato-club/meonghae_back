package com.meonghae.communityservice.unit.domain.review;

import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.domain.review.ReviewReaction;
import com.meonghae.communityservice.dto.review.ReviewRequest;
import org.junit.jupiter.api.Test;

import static com.meonghae.communityservice.domain.review.ReviewCatalog.COLLAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReviewReactionTest {

    @Test
    public void 리뷰에_좋아요를_할_수_있다() throws Exception {
        //given
        ReviewRequest request = ReviewRequest.builder()
                .title("test1")
                .content("test review 1")
                .rating(10)
                .build();

        Review review = Review.create(request, COLLAR, "test@naver.com");

        //when
        ReviewReaction reaction = ReviewReaction.create("reactor@naver.com", review, true);


        //then
        assertThat(reaction.getReview()).isEqualTo(review);
        assertThat(reaction.getReview().getLikes()).isEqualTo(1);
        assertThat(reaction.getEmail()).isEqualTo("reactor@naver.com");
        assertThat(reaction.getRecommendStatus()).isTrue();
    }

    @Test
    public void 리뷰에_싫어요를_할_수_있다() throws Exception {
        //given
        ReviewRequest request = ReviewRequest.builder()
                .title("test1")
                .content("test review 1")
                .rating(10)
                .build();

        Review review = Review.create(request, COLLAR, "test@naver.com");

        //when
        ReviewReaction reaction = ReviewReaction.create("reactor@naver.com", review, false);


        //then
        assertThat(reaction.getReview()).isEqualTo(review);
        assertThat(reaction.getReview().getDislikes()).isEqualTo(1);
        assertThat(reaction.getEmail()).isEqualTo("reactor@naver.com");
        assertThat(reaction.getRecommendStatus()).isFalse();
    }

    @Test
    public void 좋아요_상태의_반응을_싫어요로_바꿀_수_있다() throws Exception {
        //given
        ReviewRequest request = ReviewRequest.builder()
                .title("test1")
                .content("test review 1")
                .rating(10)
                .build();

        Review review = Review.create(request, COLLAR, "test@naver.com");

        ReviewReaction reaction = ReviewReaction.create("reactor@naver.com", review, true);

        //when
        reaction.updateStatus(false);

        //then
        assertThat(reaction.getRecommendStatus()).isFalse();
        assertThat(review.getLikes()).isZero();
        assertThat(review.getDislikes()).isEqualTo(1);
    }

    @Test
    public void 싫어요_상태의_반응을_좋아요로_바꿀_수_있다() throws Exception {
        //given
        ReviewRequest request = ReviewRequest.builder()
                .title("test1")
                .content("test review 1")
                .rating(10)
                .build();

        Review review = Review.create(request, COLLAR, "test@naver.com");

        ReviewReaction reaction = ReviewReaction.create("reactor@naver.com", review, false);

        //when
        reaction.updateStatus(true);

        //then
        assertThat(reaction.getRecommendStatus()).isTrue();
        assertThat(review.getLikes()).isEqualTo(1);
        assertThat(review.getDislikes()).isZero();
    }

    @Test
    public void 좋아요_상태의_반응을_지울_수_있다() throws Exception {
        //given
        ReviewRequest request = ReviewRequest.builder()
                .title("test1")
                .content("test review 1")
                .rating(10)
                .build();

        Review review = Review.create(request, COLLAR, "test@naver.com");

        ReviewReaction reaction = ReviewReaction.create("reactor@naver.com", review, true);

        //when
        reaction.updateStatus(true);

        //then
        assertThat(reaction.getRecommendStatus()).isNull();
        assertThat(review.getLikes()).isZero();
        assertThat(review.getDislikes()).isZero();
    }

    @Test
    public void 싫어요_상태의_반응을_지울_수_있다() throws Exception {
        //given
        ReviewRequest request = ReviewRequest.builder()
                .title("test1")
                .content("test review 1")
                .rating(10)
                .build();

        Review review = Review.create(request, COLLAR, "test@naver.com");

        ReviewReaction reaction = ReviewReaction.create("reactor@naver.com", review, false);

        //when
        reaction.updateStatus(false);

        //then
        assertThat(reaction.getRecommendStatus()).isNull();
        assertThat(review.getLikes()).isZero();
        assertThat(review.getDislikes()).isZero();
    }
}