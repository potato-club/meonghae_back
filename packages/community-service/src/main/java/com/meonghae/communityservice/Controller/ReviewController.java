package com.meonghae.communityservice.Controller;

import com.meonghae.communityservice.Dto.ReviewDto.ReviewListDto;
import com.meonghae.communityservice.Dto.ReviewDto.ReviewRequestDto;
import com.meonghae.communityservice.Service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Api(value = "REVIEW_CONTROLLER", tags = "리뷰 서비스 컨트롤러")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "타입별 리뷰 조회 API")
    @GetMapping("/{type}")
    public ResponseEntity<Slice<ReviewListDto>> getReviewList(@PathVariable(name = "type") int type,
                                                              @RequestParam(value = "p",
                                                                     defaultValue = "1",
                                                                      required = false) int page,
                                                              @RequestParam(value = "keyword",
                                                              required = false) String keyword,
                                                              @RequestParam(value = "sort",
                                                                      defaultValue = "latest",
                                                                      required = false) String sort
                                                              ) {
        Slice<ReviewListDto> listDto = reviewService.getReviewByType(type, page, keyword, sort);
        return ResponseEntity.ok(listDto);
    }

    @Operation(summary = "리뷰 생성 API")
    @PostMapping("")
    public ResponseEntity<String> addReview(@RequestParam(value = "type") int type,
                                            @Valid @RequestBody ReviewRequestDto requestDto) {
        reviewService.createReview(type, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 등록 완료");
    }
}
