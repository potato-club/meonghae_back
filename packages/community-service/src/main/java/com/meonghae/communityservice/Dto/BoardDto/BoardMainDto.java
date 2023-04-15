package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Enum.BoardType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardMainDto {
    private Long id;
    private String title;
    private BoardType type;
    private String userId;
    private int likes;
    private int commentSize;
    private boolean image;

    public BoardMainDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.type = board.getType();
        this.userId = board.getOwner();
        this.commentSize = board.getComments().isEmpty() ? 0 : board.getComments().size();
        this.likes = board.getLikes();
        this.image = !board.getImages().isEmpty();
    }
}
