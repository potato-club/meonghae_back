package com.meonghae.communityservice.web.review;

import com.meonghae.communityservice.dto.review.ReviewList;
import com.meonghae.communityservice.dto.review.ReviewReactionType;
import com.meonghae.communityservice.dto.review.ReviewRequest;
import com.meonghae.communityservice.application.review.ReviewReactionService;
import com.meonghae.communityservice.application.review.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Slf4j
@Api(value = "REVIEW_CONTROLLER", tags = "리뷰 서비스 컨트롤러")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewReactionService reactionService;

    @Operation(summary = "타입별 리뷰 조회 API")
    @GetMapping("/{type}")
    public ResponseEntity<Slice<ReviewList>> getReviewList(@PathVariable(name = "type") int type,
                                                           @RequestHeader("Authorization") String token,
                                                           @RequestParam(value = "p",
                                                                     defaultValue = "1",
                                                                      required = false) int page,
                                                           @RequestParam(value = "keyword",
                                                              required = false) String keyword,
                                                           @RequestParam(value = "sort",
                                                                      defaultValue = "LATEST",
                                                                      required = false) String sort,
                                                           @RequestParam(value = "photo",
                                                              defaultValue = "false",
                                                              required = false) boolean photoOnly
                                                              ) {
        Slice<ReviewList> listDto = reviewService.getReviewByType(type, token, page, keyword, sort, photoOnly);
        return ResponseEntity.ok(listDto);
    }

    @Operation(summary = "리뷰 생성 API")
    @PostMapping("")
    public ResponseEntity<String> addReview(@RequestParam(value = "type") int type,
                                            @Valid ReviewRequest requestDto,
                                            @RequestHeader("Authorization") String token) {
        reviewService.createReview(type, requestDto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 등록 완료");
    }

    @Operation(summary = "리뷰 추천 API")
    @PostMapping("/{reviewId}/recommend")
    public ResponseEntity<String> likeReview(@PathVariable(name = "reviewId") Long reviewId,
                                             @RequestHeader("Authorization") String token,
                                             @RequestBody ReviewReactionType typeDto) {
        String result = reactionService.toggleRecommendedReview(reviewId, token, typeDto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "리뷰 삭제 API")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable(name = "reviewId") Long reviewId,
                                               @RequestHeader("Authorization") String token) {
        reviewService.deleteReview(reviewId, token);
        return ResponseEntity.ok("삭제 완료");
    }
}
