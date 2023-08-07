package com.meonghae.communityservice.Repository;

import com.meonghae.communityservice.Entity.Review.Review;
import com.meonghae.communityservice.Entity.Review.ReviewReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long> {
    ReviewReaction findByEmailAndReview(String email, Review review);
    List<ReviewReaction> findByEmailAndReviewIdIn(String email, List<Long> reviewIds);
}
