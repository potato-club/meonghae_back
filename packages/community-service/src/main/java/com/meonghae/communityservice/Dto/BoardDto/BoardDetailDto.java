package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardDetailDto {
    private Long id;
    private String userId;
    private String title;
    private String content;
    private List<BoardImage> images;
    private int commentSize;
    private int likes;

    public BoardDetailDto(Board board) {
        this.id = board.getId();
        this.userId = board.getOwner();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.commentSize = board.getComments().size();
        this.images = board.getImages();
        this.likes = board.getLikes();
    }
}
