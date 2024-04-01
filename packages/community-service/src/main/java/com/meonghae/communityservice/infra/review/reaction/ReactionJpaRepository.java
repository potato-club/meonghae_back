package com.meonghae.communityservice.infra.review.reaction;

import com.meonghae.communityservice.infra.review.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReactionJpaRepository extends JpaRepository<ReactionEntity, Long> {
    ReactionEntity findByEmailAndReviewEntity_Id(String email, Long reviewId);
    List<ReactionEntity> findByEmailAndReviewEntityIdIn(String email, List<Long> reviewIds);
}
