package com.meonghae.communityservice.application.review.port;

import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.domain.review.ReviewCatalog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ReviewRepository {
    Optional<Review> findById(Long reviewId);
    Slice<Review> findByCatalogAndKeywordAndSortType(Pageable pageable, ReviewCatalog catalog, String keyword);
    Slice<Review> findByCatalogAndHasImageAndKeywordAndSortType(Pageable pageable, ReviewCatalog catalog, String keyword);
    Review save(Review review);
    Review update(Review review);

    void delete(Review review);

    void updateReaction(Review review);
}
