package com.meonghae.communityservice.Entity.Review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ReviewReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Review review;

    @Column
    private Boolean recommendStatus;

    public ReviewReaction(String email, Review review, Boolean isLike) {
        this.email = email;
        this.review = review;
        this.recommendStatus = isLike;
    }

    public String updateStatus(Boolean isLike) {
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
                review.addLikes();
                return "추천 완료";
            } else if (oldStatus) {
                this.recommendStatus = null;
                review.cancelLikes();
                return "추천 취소";
            } else {
                review.cancelDislikes();
                review.addLikes();
                return "추천 완료";
            }
        } else {
            if(oldStatus == null) {
                review.addDislikes();
                return "비추 완료";
            } else if (oldStatus) {
                review.cancelLikes();
                review.addDislikes();
                return "비추 완료";
            } else {
                this.recommendStatus = null;
                review.cancelDislikes();
                return "비추 취소";
            }
        }
    }
}
