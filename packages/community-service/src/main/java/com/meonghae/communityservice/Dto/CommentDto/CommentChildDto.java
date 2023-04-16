package com.meonghae.communityservice.Dto.CommentDto;

import com.meonghae.communityservice.Entity.Board.BoardComment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentChildDto {
    private Long id;
    private String userId;
    private String comment;
    private Boolean update;
    private Long parentId;

    public CommentChildDto(BoardComment child) {
        this.id = child.getId();
        this.comment = child.getComment();
        this.userId = child.getUserId();
        this.update = child.getUpdated();
        this.parentId = child.getParent().getId();
    }
}
