package com.meonghae.communityservice.application.review;

import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.application.port.S3ServicePort;
import com.meonghae.communityservice.application.port.UserServicePort;
import com.meonghae.communityservice.application.review.port.ReviewRepository;
import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.dto.review.ReviewListDto;
import com.meonghae.communityservice.dto.review.ReviewRequest;
import com.meonghae.communityservice.dto.s3.S3Request;
import com.meonghae.communityservice.dto.s3.S3Response;
import com.meonghae.communityservice.domain.review.RecommendStatus;
import com.meonghae.communityservice.domain.review.ReviewCatalog;
import com.meonghae.communityservice.exception.custom.ReviewException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.meonghae.communityservice.exception.error.ErrorCode.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@Builder
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewReactionService reactionService;
    private final RedisPort redisService;
    private final UserServicePort userService;
    private final S3ServicePort s3Service;

    public Slice<ReviewListDto> getReviewByType(int key, String token, int page,
                                                String keyword, String sort, boolean photoOnly) {
        ReviewCatalog catalog = ReviewCatalog.findWithKey(key);
        if(catalog == null) throw new ReviewException(BAD_REQUEST, "잘못된 Catalog Type 입니다.");
        ReviewSortType sortType = ReviewSortType.findType(sort);
        Slice<Review> reviews;

        reviews = photoOnly ?
                getPagingReviewWithPhoto(page, catalog, keyword, sortType)
                : getPagingReview(page, catalog, keyword, sortType);

        List<Long> reviewIds = reviews.getContent().stream().map(Review::getId).collect(Collectors.toList());
        Map<Long, RecommendStatus> reactions = reactionService.getReviewReactions(reviewIds, token);

        String email = userService.getUserEmail(token);

        return reviews.map(r -> convertTypeAndAddImage(r, email, reactions.get(r.getId())));
    }
    private Slice<Review> getPagingReview(int page, ReviewCatalog catalog, String keyword, ReviewSortType sort) {
        PageRequest request;
        switch (sort) {
            case RATING_ASC:
                request = PageRequest.of(page - 1, 15, Sort.by(Sort.Direction.ASC, "rating")
                        .and(Sort.by(Sort.Direction.DESC, "createdDate")));
                return reviewRepository.findByCatalogAndKeywordAndSortType(request, catalog, keyword);
            case RATING_DESC:
                request = PageRequest.of(page - 1, 15, Sort.by(Sort.Direction.DESC, "rating")
                        .and(Sort.by(Sort.Direction.DESC, "createdDate")));
                return reviewRepository.findByCatalogAndKeywordAndSortType(request, catalog, keyword);
            case RECOMMEND:
                request = PageRequest.of(page - 1, 15, Sort.by(Sort.Direction.DESC, "likes")
                        .and(Sort.by(Sort.Direction.DESC, "createdDate")));
                return reviewRepository.findByCatalogAndKeywordAndSortType(request, catalog, keyword);
            case LATEST:
            default:
                request = PageRequest.of(page - 1, 15,
                        Sort.by(Sort.Direction.DESC, "createdDate"));
                return reviewRepository.findByCatalogAndKeywordAndSortType(request, catalog, keyword);
        }
    }
    private Slice<Review> getPagingReviewWithPhoto(int page, ReviewCatalog catalog, String keyword, ReviewSortType sort) {
        PageRequest request;
        switch (sort) {
            case RATING_ASC:
                request = PageRequest.of(page - 1, 15, Sort.by(Sort.Direction.ASC, "rating")
                        .and(Sort.by(Sort.Direction.DESC, "createdDate")));
                return reviewRepository.findByCatalogAndHasImageAndKeywordAndSortType(request, catalog, keyword);
            case RATING_DESC:
                request = PageRequest.of(page - 1, 15, Sort.by(Sort.Direction.DESC, "rating")
                        .and(Sort.by(Sort.Direction.DESC, "createdDate")));
                return reviewRepository.findByCatalogAndHasImageAndKeywordAndSortType(request, catalog, keyword);
            case RECOMMEND:
                request = PageRequest.of(page - 1, 15, Sort.by(Sort.Direction.DESC, "likes")
                        .and(Sort.by(Sort.Direction.DESC, "createdDate")));
                return reviewRepository.findByCatalogAndHasImageAndKeywordAndSortType(request, catalog, keyword);
            case LATEST:
            default:
                request = PageRequest.of(page - 1, 15,
                        Sort.by(Sort.Direction.DESC, "createdDate"));
                return reviewRepository.findByCatalogAndHasImageAndKeywordAndSortType(request, catalog, keyword);
        }
    }

    @Transactional
    public Review createReview(int key, ReviewRequest requestDto, String token) {
        ReviewCatalog catalog = ReviewCatalog.findWithKey(key);
        String email = userService.getUserEmail(token);
        if(catalog == null) throw new ReviewException(BAD_REQUEST, "잘못된 Catalog Type 입니다.");

        Review review = Review.create(requestDto, catalog, email);
        Review savedReview = reviewRepository.save(review);

        List<MultipartFile> images = requestDto.getImages();
        if(images != null) {
            if(images.size() > 3) throw new ReviewException(BAD_REQUEST, "리뷰 사진은 3개까지 업로드 가능합니다.");
            S3Request s3Dto = new S3Request(savedReview.getId(), "REVIEW");
            s3Service.uploadImage(images, s3Dto);
            savedReview.setHasImage();
            reviewRepository.save(savedReview);
        }

        return savedReview;
    }

    @Transactional
    public void deleteReview(Long reviewId, String token) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NOT_FOUND, "review is not exist"));
        String email = userService.getUserEmail(token);
        if(!review.getEmail().equals(email)) {
            throw new ReviewException(UNAUTHORIZED, "리뷰 작성자만 삭제 가능합니다.");
        }
        S3Request requestDto = new S3Request(review.getId(), "REVIEW");
        s3Service.deleteImage(requestDto);
        reviewRepository.delete(review);
    }

    private ReviewListDto convertTypeAndAddImage(Review review, String email, RecommendStatus status) {
        String nickname = redisService.getNickname(review.getEmail());
        String url = redisService.getProfileImage(review.getEmail());
        boolean isWriter = Objects.equals(review.getEmail(), email);
        ReviewListDto reviewDto = new ReviewListDto(review, nickname, url, status, isWriter);
        if (review.getHasImage()) {
            List<S3Response> reviewImages = redisService.getReviewImages(review.getId());
            reviewDto.setImages(reviewImages);
        }
        return reviewDto;
    }
}
