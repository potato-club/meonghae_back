package com.meonghae.communityservice.Dto.ReviewDto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ReviewRequestDto {
    @NotBlank(message = "제목은 반드시 필요합니다.")
    private String title;
    @NotBlank(message = "내용은 반드시 필요합니다.")
    @Size(min = 5, message = "내용은 5자 이상 작성해야 합니다.")
    private String content;
    @NotBlank(message = "유저 아이디는 반드시 필요합니다.")
    private String userId;
    @NotNull(message = "별점은 반드시 필요합니다.")
    @DecimalMin(value = "0")
    @DecimalMax(value = "10")
    private int rating;
}
