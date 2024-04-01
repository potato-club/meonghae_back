package com.meonghae.communityservice.infra.review.review;

import com.meonghae.communityservice.application.review.port.ReviewRepository;
import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.domain.review.ReviewCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Optional<Review> findById(Long reviewId) {
        return reviewJpaRepository.findById(reviewId).map(ReviewEntity::toModel);
    }

    @Override
    public Slice<Review> findByCatalogAndKeywordAndSortType(Pageable pageable, ReviewCatalog catalog, String keyword) {
        return reviewJpaRepository.findByCatalogAndKeywordAndSortType(pageable, catalog, keyword)
                .map(ReviewEntity::toModel);
    }

    @Override
    public Slice<Review> findByCatalogAndHasImageAndKeywordAndSortType(Pageable pageable, ReviewCatalog catalog, String keyword) {
        return reviewJpaRepository.findByCatalogAndHasImageAndKeywordAndSortType(pageable, catalog, keyword)
                .map(ReviewEntity::toModel);
    }

    @Override
    public Review save(Review review) {
        return reviewJpaRepository.save(ReviewEntity.fromModel(review)).toModel();
    }

    @Override
    public void delete(Review review) {
        reviewJpaRepository.delete(ReviewEntity.fromModel(review));
    }
}
