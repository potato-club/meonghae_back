package com.meonghae.communityservice.Dto.CommentDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {
    private String userId;
    private String comment;
}
