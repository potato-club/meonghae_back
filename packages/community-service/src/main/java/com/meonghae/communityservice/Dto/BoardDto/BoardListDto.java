package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Entity.Board.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardListDto {
    private Long id;
    private String userId;
    private String title;
    private String content;
    private int likes;
    private int comments;
    private boolean hasImage;

    public BoardListDto(Board board) {
        this.id = board.getId();
        this.userId = board.getOwner();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.likes = board.getLikes();
        this.comments = board.getComments().size();
        this.hasImage = !board.getImages().isEmpty();
    }
}
