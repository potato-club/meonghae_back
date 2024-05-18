package com.meonghae.communityservice.application.review.port;

import com.meonghae.communityservice.domain.review.ReviewReaction;

import java.util.List;

public interface ReactionRepository {
    ReviewReaction findByEmailAndReviewEntity_Id(String email, Long reviewId);
    List<ReviewReaction> findByEmailAndReviewEntityIdIn(String email, List<Long> reviewIds);
    ReviewReaction save(ReviewReaction reaction);
}
