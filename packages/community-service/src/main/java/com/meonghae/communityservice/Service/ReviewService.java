package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.S3ServiceClient;
import com.meonghae.communityservice.Client.UserServiceClient;
import com.meonghae.communityservice.Dto.ReviewDto.ReviewListDto;
import com.meonghae.communityservice.Dto.ReviewDto.ReviewRequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3RequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3ResponseDto;
import com.meonghae.communityservice.Entity.Review.Review;
import com.meonghae.communityservice.Enum.RecommendStatus;
import com.meonghae.communityservice.Enum.ReviewCatalog;
import com.meonghae.communityservice.Enum.ReviewSortType;
import com.meonghae.communityservice.Exception.Custom.BoardException;
import com.meonghae.communityservice.Exception.Custom.ReviewException;
import com.meonghae.communityservice.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meonghae.communityservice.Exception.Error.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewReactionService reactionService;
    private final RedisService redisService;
    private final UserServiceClient userService;
    private final S3ServiceClient s3Service;

    @Transactional
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

        return reviews.map(r -> convertTypeAndAddImage(r, token, reactions.get(r.getId())));
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
    public void createReview(int key, ReviewRequestDto requestDto, String token) {
        ReviewCatalog catalog = ReviewCatalog.findWithKey(key);
        String email = userService.getUserEmail(token);
        if(catalog == null) throw new ReviewException(BAD_REQUEST, "잘못된 Catalog Type 입니다.");
        Review review = Review.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .email(email)
                .rating(requestDto.getRating())
                .catalog(catalog)
                .likes(0)
                .dislikes(0)
                .hasImage(false).build();
        Review saveReview = reviewRepository.save(review);
        List<MultipartFile> images = requestDto.getImages();
        if(images != null) {
            if(images.size() > 3) throw new ReviewException(BAD_REQUEST, "리뷰 사진은 3개까지 업로드 가능합니다.");
            S3RequestDto s3Dto = new S3RequestDto(saveReview.getId(), "REVIEW");
            s3Service.uploadImage(images, s3Dto);
            saveReview.setHasImage();
        }
    }

    @Transactional
    public void deleteReview(Long reviewId, String token) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NOT_FOUND, "review is not exist"));
        String email = userService.getUserEmail(token);
        if(!review.getEmail().equals(email)) {
            throw new BoardException(UNAUTHORIZED, "리뷰 작성자만 삭제 가능합니다.");
        }
        S3RequestDto requestDto = new S3RequestDto(review.getId(), "REVIEW");
        s3Service.deleteImage(requestDto);
        reviewRepository.delete(review);
    }

    private ReviewListDto convertTypeAndAddImage(Review review, String token, RecommendStatus status) {
        String nickname = redisService.getNickname(review.getEmail());
        String url = redisService.getProfileImage(review.getEmail());
        ReviewListDto reviewDto = new ReviewListDto(review, nickname, url, status);
        if (review.getHasImage()) {
            List<S3ResponseDto> reviewImages = redisService.getReviewImages(review.getId());
            reviewDto.setImages(reviewImages);
        }
        return reviewDto;
    }
}
