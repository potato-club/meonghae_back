package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Enum.BoardType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardMainDto {
    private String title;
    private BoardType type;
    private String userId;
    private int likes;
    private int commentSize;
    private boolean image;

    public BoardMainDto(Board board) {
        this.title = board.getTitle();
        this.type = board.getType();
        this.userId = board.getOwner();

        if (board.getComments().isEmpty()) {
            this.commentSize = 0;
        } else {
            this.commentSize = board.getComments().size();
        }
//        this.commentSize = board.getComments().size();
        this.likes = board.getLikes();
        this.image = !board.getImages().isEmpty();
    }
}
