package com.meonghae.communityservice.infra.review.review;

import com.meonghae.communityservice.domain.review.ReviewCatalog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, Long> {

    @Query("SELECT r FROM ReviewEntity r WHERE r.catalog = :catalog AND " +
            "(:keyword is null OR r.title LIKE %:keyword%) ")
    Slice<ReviewEntity> findByCatalogAndKeywordAndSortType(Pageable pageable, ReviewCatalog catalog, String keyword);

    @Query("SELECT r FROM ReviewEntity r WHERE r.catalog = :catalog AND " +
            "r.hasImage = true AND (:keyword is null OR r.title LIKE %:keyword%) ")
    Slice<ReviewEntity> findByCatalogAndHasImageAndKeywordAndSortType(Pageable pageable, ReviewCatalog catalog, String keyword);
}
