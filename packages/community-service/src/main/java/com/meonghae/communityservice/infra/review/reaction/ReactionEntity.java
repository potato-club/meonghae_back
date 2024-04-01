package com.meonghae.communityservice.infra.review.reaction;

import com.meonghae.communityservice.domain.review.ReviewReaction;
import com.meonghae.communityservice.infra.review.review.ReviewEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_reaction")
public class ReactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ReviewEntity reviewEntity;

    @Column
    private Boolean recommendStatus;

    public ReactionEntity(String email, ReviewEntity reviewEntity, Boolean isLike) {
        this.email = email;
        this.reviewEntity = reviewEntity;
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
                reviewEntity.addLikes();
                return "추천 완료";
            } else if (oldStatus) {
                this.recommendStatus = null;
                reviewEntity.cancelLikes();
                return "추천 취소";
            } else {
                reviewEntity.cancelDislikes();
                reviewEntity.addLikes();
                return "추천 완료";
            }
        } else {
            if(oldStatus == null) {
                reviewEntity.addDislikes();
                return "비추 완료";
            } else if (oldStatus) {
                reviewEntity.cancelLikes();
                reviewEntity.addDislikes();
                return "비추 완료";
            } else {
                this.recommendStatus = null;
                reviewEntity.cancelDislikes();
                return "비추 취소";
            }
        }
    }

    public static ReactionEntity fromModel(ReviewReaction reaction) {
        ReactionEntity entity = new ReactionEntity();
        entity.id = reaction.getId();
        entity.email = reaction.getEmail();
        entity.recommendStatus = reaction.getRecommendStatus();
        entity.reviewEntity = ReviewEntity.fromModel(reaction.getReview());
        return entity;
    }

    public ReviewReaction toModel() {
        return ReviewReaction.builder()
                .id(this.id)
                .email(this.email)
                .review(this.reviewEntity.toModel())
                .recommendStatus(this.recommendStatus)
                .build();
    }
}
