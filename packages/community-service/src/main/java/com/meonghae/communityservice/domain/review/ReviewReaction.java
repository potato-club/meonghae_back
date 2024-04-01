package com.meonghae.communityservice.domain.review;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewReaction {
    private final Long id;
    private final String email;
    private final Review review;
    private Boolean recommendStatus;

    @Builder
    public ReviewReaction(Long id, String email, Review review, Boolean recommendStatus) {
        this.id = id;
        this.email = email;
        this.review = review;
        this.recommendStatus = recommendStatus;
    }

    public static ReviewReaction create(String email, Review review, Boolean isLikes) {
        if (isLikes) {
            review.addLikes();
        } else {
            review.addDislikes();
        }

        return ReviewReaction.builder()
                .email(email)
                .review(review)
                .recommendStatus(isLikes)
                .build();
    }

    public void updateStatus(Boolean isLike) {
        Boolean oldStatus = this.getRecommendStatus();
        this.recommendStatus = isLike;

        // 1. 좋아요 눌렀을 때 => boolean 값 판단
        // 1-1. 좋아요 취소 (상태 true 인 경우)
        // 1-2. 좋아요 반영 (상태 null 인 경우)
        // 1-3. 좋아요 반영 및 싫어요 취소 (상태 false 인 경우)

        // 2. 싫어요 눌렀을 때
        // 2-1. 싫어요 취소 (상태 false 인 경우)
        // 2-2. 싫어요 반영 (상태 null 인 경우)
        // 2-3. 싫어요 반영 및 좋아요 취소 (상태 true 인 경우)
        if(isLike) {
            if(oldStatus == null) {
                this.review.addLikes();
            } else if (oldStatus) {
                this.recommendStatus = null;
                this.review.cancelLikes();
            } else {
                this.review.cancelDislikes();
                this.review.addLikes();
            }
        } else {
            if(oldStatus == null) {
                this.review.addDislikes();
            } else if (oldStatus) {
                this.review.cancelLikes();
                this.review.addDislikes();
            } else {
                this.recommendStatus = null;
                this.review.cancelDislikes();
            }
        }
    }
}
