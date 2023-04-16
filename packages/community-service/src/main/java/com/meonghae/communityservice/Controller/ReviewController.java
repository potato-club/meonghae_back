package com.meonghae.communityservice.Controller;

import com.meonghae.communityservice.Dto.ReviewDto.ReviewListDto;
import com.meonghae.communityservice.Dto.ReviewDto.ReviewRequestDto;
import com.meonghae.communityservice.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
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

    @PostMapping("")
    public ResponseEntity<String> addReview(@RequestParam(value = "type") int type,
                                            @Valid @RequestBody ReviewRequestDto requestDto) {
        reviewService.createReview(type, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 등록 완료");
    }
}
