package com.meonghae.communityservice.Dto;

import com.meonghae.communityservice.Entity.Board.BoardComment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String userId;
    private String comment;
    private List<CommentResponseDto> replies;

    public CommentResponseDto(BoardComment comment) {
        this.id = comment.getId();
        this.userId = comment.getUserId();
        this.comment = comment.getComment();
        this.replies = comment.getReplies().stream().map(CommentResponseDto::new).collect(Collectors.toList());
    }
}
