package com.meonghae.communityservice.Repository;

import com.meonghae.communityservice.Entity.Review.Review;
import com.meonghae.communityservice.Enum.ReviewCatalog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.catalog = :catalog AND " +
            "(:keyword is null OR r.title LIKE %:keyword%) ")
    Slice<Review> findByCatalogAndKeywordAndSortType(Pageable pageable, ReviewCatalog catalog, String keyword);

    @Query("SELECT r FROM Review r WHERE r.catalog = :catalog AND " +
            "r.hasImage = true AND (:keyword is null OR r.title LIKE %:keyword%) ")
    Slice<Review> findByCatalogAndHasImageAndKeywordAndSortType(Pageable pageable, ReviewCatalog catalog, String keyword);
}
