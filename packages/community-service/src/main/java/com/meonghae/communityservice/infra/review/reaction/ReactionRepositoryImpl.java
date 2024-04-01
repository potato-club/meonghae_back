package com.meonghae.communityservice.infra.review.reaction;

import com.meonghae.communityservice.application.review.port.ReactionRepository;
import com.meonghae.communityservice.domain.review.ReviewReaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReactionRepositoryImpl implements ReactionRepository {

    private final ReactionJpaRepository reactionJpaRepository;

    @Override
    public ReviewReaction findByEmailAndReviewEntity_Id(String email, Long reviewId) {
        return reactionJpaRepository.findByEmailAndReviewEntity_Id(email, reviewId)
                .toModel();
    }

    @Override
    public List<ReviewReaction> findByEmailAndReviewEntityIdIn(String email, List<Long> reviewIds) {
        return reactionJpaRepository.findByEmailAndReviewEntityIdIn(email, reviewIds)
                .stream().map(ReactionEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewReaction save(ReviewReaction reaction) {
        return reactionJpaRepository.save(ReactionEntity.fromModel(reaction)).toModel();
    }
}
