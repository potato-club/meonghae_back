package com.meonghae.communityservice.Dto;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardComment;
import com.meonghae.communityservice.Entity.Board.BoardImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BoardDetailDto {
    private Long id;
    private String userId;
    private String title;
    private String content;
    private List<CommentResponseDto> comments;
    private List<BoardImage> images;
    private int likes;

    public BoardDetailDto(Board board) {
        this.id = board.getId();
        this.userId = board.getOwner();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.comments = board.getComments().stream().filter(BoardComment::isParent)
                .map(CommentResponseDto::new).collect(Collectors.toList());
        this.images = board.getImages();
        this.likes = board.getLikes();
    }
}
