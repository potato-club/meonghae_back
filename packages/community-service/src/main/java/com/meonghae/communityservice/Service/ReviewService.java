package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Dto.ReviewDto.ReviewListDto;
import com.meonghae.communityservice.Dto.ReviewDto.ReviewRequestDto;
import com.meonghae.communityservice.Entity.Review.Review;
import com.meonghae.communityservice.Enum.ReviewCatalog;
import com.meonghae.communityservice.Exception.Custom.ReviewException;
import com.meonghae.communityservice.Exception.Error.ErrorCode;
import com.meonghae.communityservice.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Transactional
    public Slice<ReviewListDto> getReviewByType(int key, int page, String keyword, String sort) {
        ReviewCatalog catalog = ReviewCatalog.findWithKey(key);
        if(catalog == null) throw new ReviewException(ErrorCode.BAD_REQUEST, "잘못된 Catalog Type 입니다.");
        PageRequest request = PageRequest.of(page - 1, 20);
        Slice<Review> reviews;
        if(sort.equals("rating_asc")) {
            request = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.ASC, "rating")
                    .and(Sort.by(Sort.Direction.DESC, "createdDate")));
            reviews = reviewRepository.findByCatalogAndKeywordAndRating(request, catalog, keyword);
        } else if(sort.equals("rating_desc")) {
            request = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "rating")
                    .and(Sort.by(Sort.Direction.DESC, "createdDate")));
            reviews = reviewRepository.findByCatalogAndKeywordAndRating(request, catalog, keyword);
        } else {
            reviews = reviewRepository.findByCatalogAndKeywordAndSort(request, catalog, keyword, sort);
        }
        return reviews.map(ReviewListDto::new);
    }

    @Transactional
    public void createReview(int key, ReviewRequestDto requestDto) {
        ReviewCatalog catalog = ReviewCatalog.findWithKey(key);
        if(catalog == null) throw new ReviewException(ErrorCode.BAD_REQUEST, "잘못된 Catalog Type 입니다.");
        Review review = Review.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .userId(requestDto.getUserId())
                .rating(requestDto.getRating())
                .catalog(catalog).build();
        reviewRepository.save(review);
    }
}