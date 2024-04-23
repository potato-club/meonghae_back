package com.meonghae.communityservice.unit.domain.review;

import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.dto.review.ReviewRequest;
import org.junit.jupiter.api.Test;

import static com.meonghae.communityservice.domain.review.ReviewCatalog.COLLAR;
import static org.assertj.core.api.Assertions.assertThat;

class ReviewTest {
    @Test
    public void 리뷰를_생성할_수_있다() throws Exception {
        //given
        ReviewRequest request = ReviewRequest.builder()
                .title("test1")
                .content("test review 1")
                .rating(10)
                .build();

        //when
        Review review = Review.create(request, COLLAR, "test@naver.com");

        //then
        assertThat(review)
                .extracting("title", "content", "rating", "hasImage")
                .containsExactly("test1", "test review 1", 10, false);
    }

    @Test
    public void 리뷰의_이미지_여부_값을_수정할_수_있다() throws Exception {
        //given
        ReviewRequest request = ReviewRequest.builder()
                .title("test1")
                .content("test review 1")
                .rating(10)
                .build();

        Review review = Review.create(request, COLLAR, "test@naver.com");

        //when
        review.setHasImage();

        //then
        assertThat(review.getHasImage()).isTrue();
    }

    @Test
    public void 리뷰의_좋아요_싫어요_기능_테스트() throws Exception {
        //given
        ReviewRequest request = ReviewRequest.builder()
                .title("test1")
                .content("test review 1")
                .rating(10)
                .build();

        Review review = Review.create(request, COLLAR, "test@naver.com");

        //when
        review.addLikes();
        review.addDislikes();
        review.cancelLikes();

        //then
        assertThat(review.getLikes()).isEqualTo(0);
        assertThat(review.getDislikes()).isEqualTo(1);
    }

//    @Test
//    public void 리뷰의_좋아요_개수를_1개_증가시킨다() throws Exception {
//        //given
//        ReviewRequest request = ReviewRequest.builder()
//                .title("test1")
//                .content("test review 1")
//                .rating(10)
//                .build();
//
//        Review review = Review.create(request, COLLAR, "test@naver.com");
//
//        //when
//        review.addLikes();
//
//        //then
//        assertThat(review.getLikes()).isEqualTo(1);
//    }
//
//    @Test
//    public void 리뷰의_좋아요_개수를_1개_감소시킨다() throws Exception {
//        //given
//        ReviewRequest request = ReviewRequest.builder()
//                .title("test1")
//                .content("test review 1")
//                .rating(10)
//                .build();
//
//        Review review = Review.create(request, COLLAR, "test@naver.com");
//
//        review.addLikes();
//        assertThat(review.getLikes()).isEqualTo(1);
//
//        //when
//        review.cancelLikes();
//
//        //then
//        assertThat(review.getLikes()).isZero();
//    }
//
//    @Test
//    public void 리뷰의_싫어요_개수를_1개_증가시킨다() throws Exception {
//        //given
//        ReviewRequest request = ReviewRequest.builder()
//                .title("test1")
//                .content("test review 1")
//                .rating(10)
//                .build();
//
//        Review review = Review.create(request, COLLAR, "test@naver.com");
//
//        //when
//        review.addDislikes();
//
//        //then
//        assertThat(review.getDislikes()).isEqualTo(1);
//    }
//
//    @Test
//    public void 리뷰의_싫어요_개수를_1개_감소시킨다() throws Exception {
//        //given
//        ReviewRequest request = ReviewRequest.builder()
//                .title("test1")
//                .content("test review 1")
//                .rating(10)
//                .build();
//
//        Review review = Review.create(request, COLLAR, "test@naver.com");
//
//        review.addDislikes();
//        assertThat(review.getDislikes()).isEqualTo(1);
//
//        //when
//        review.cancelDislikes();
//
//        //then
//        assertThat(review.getDislikes()).isZero();
//    }
}