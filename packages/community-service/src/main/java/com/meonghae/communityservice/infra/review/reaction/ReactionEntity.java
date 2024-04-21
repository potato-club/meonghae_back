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
