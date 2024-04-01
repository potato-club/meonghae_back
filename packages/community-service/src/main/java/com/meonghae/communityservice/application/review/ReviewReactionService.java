package com.meonghae.communityservice.application.review;

import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.application.review.port.ReactionRepository;
import com.meonghae.communityservice.application.review.port.ReviewRepository;
import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.domain.review.ReviewReaction;
import com.meonghae.communityservice.dto.review.ReviewReactionType;
import com.meonghae.communityservice.domain.review.RecommendStatus;
import com.meonghae.communityservice.exception.custom.ReviewException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.meonghae.communityservice.domain.review.RecommendStatus.*;
import static com.meonghae.communityservice.exception.error.ErrorCode.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@Builder
@RequiredArgsConstructor
public class ReviewReactionService {
    private final ReactionRepository reactionRepository;
    private final ReviewRepository reviewRepository;
    private final UserServicePort userService;

    @Transactional
    public String toggleRecommendedReview(Long reviewId, String token, ReviewReactionType typeDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NOT_FOUND, "review is not exist"));

        String email = userService.getUserEmail(token);
        ReviewReaction reaction = reactionRepository.findByEmailAndReviewEntity_Id(email, reviewId);
        Boolean isLikes = typeDto.getIsLike();
        if (reaction == null) {
            ReviewReaction newReaction = ReviewReaction.create(email, review, isLikes);
            reactionRepository.save(newReaction);
            if (isLikes) {
                return "추천 완료";
            } else {
                return "비추 완료";
            }
        } else {
            String res = getMessageForReactionChange(reaction, isLikes);
            reaction.updateStatus(isLikes);
            reactionRepository.save(reaction);
            return res;
        }
    }

    private String getMessageForReactionChange(ReviewReaction reaction, Boolean isLike) {
        Boolean oldStatus = reaction.getRecommendStatus();

        if(isLike) {
            if(oldStatus == null || !oldStatus) {
                return "추천 완료";
            } else {
                return "추천 취소";
            }
        } else {
            if(oldStatus == null || oldStatus) {
                return "비추 완료";
            } else {
                return "비추 취소";
            }
        }
    }

    public Map<Long, RecommendStatus> getReviewReactions(List<Long> reviewIds, String token) {
        String email = userService.getUserEmail(token);
        List<ReviewReaction> reactions = reactionRepository.findByEmailAndReviewEntityIdIn(email, reviewIds);
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

// 성능 문제 발생 => 쿼리 튜닝을 위한 로직 수정
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
