package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.UserServiceClient;
import com.meonghae.communityservice.Dto.ReviewDto.ReviewReactionTypeDto;
import com.meonghae.communityservice.Entity.Review.Review;
import com.meonghae.communityservice.Entity.Review.ReviewReaction;
import com.meonghae.communityservice.Enum.RecommendStatus;
import com.meonghae.communityservice.Exception.Custom.ReviewException;
import com.meonghae.communityservice.Repository.ReviewReactionRepository;
import com.meonghae.communityservice.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.meonghae.communityservice.Enum.RecommendStatus.*;
import static com.meonghae.communityservice.Exception.Error.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReviewReactionService {
    private final ReviewReactionRepository reactionRepository;
    private final ReviewRepository reviewRepository;
    private final UserServiceClient userService;

    public String toggleRecommendedReview(Long reviewId, String token, ReviewReactionTypeDto typeDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(BAD_REQUEST, "review is not exist"));

        String email = userService.getUserEmail(token);
        ReviewReaction reaction = reactionRepository.findByEmailAndReview(email, review);
        Boolean isLikes = typeDto.getIsLike();
        if (reaction == null) {
            ReviewReaction newReaction = new ReviewReaction(email, review, isLikes);
            reactionRepository.save(newReaction);
            if (isLikes) {
                review.addLikes();
                return "추천 완료";
            } else {
                review.addDislikes();
                return "비추 완료";
            }
        } else {
            return reaction.updateStatus(isLikes);
        }
    }

    public Map<Long, RecommendStatus> getReviewReactions(List<Long> reviewIds, String token) {
        String email = userService.getUserEmail(token);
        List<ReviewReaction> reactions = reactionRepository.findByEmailAndReviewIdIn(email, reviewIds);
        Map<Long, RecommendStatus> reviewReactions = new HashMap<>();
        for (ReviewReaction reaction : reactions) {
            RecommendStatus status;
            if(reaction.getRecommendStatus() == null) {
                status = NONE;
            } else if (reaction.getRecommendStatus()) {
                status = TRUE;
            } else {
                status = FALSE;
            }
            reviewReactions.put(reaction.getReview().getId(), status);
        }
        return reviewReactions;
    }
//
//    public RecommendStatus getReviewReaction(Review review, String token) {
//        String email = userService.getUserEmail(token);
//        ReviewReaction reaction = reactionRepository.findByEmailAndReview(email, review);
//        if(reaction == null || reaction.getRecommendStatus() == null) {
//            return NONE;
//        } else if (reaction.getRecommendStatus()) {
//            return TRUE;
//        } else {
//            return FALSE;
//        }
//    }
}
