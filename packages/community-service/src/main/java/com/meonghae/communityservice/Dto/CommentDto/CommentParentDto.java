package com.meonghae.communityservice.Dto.CommentDto;

import com.meonghae.communityservice.Entity.Board.BoardComment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

@Getter
@NoArgsConstructor
public class CommentParentDto {
    private Long id;
    private String userId;
    private String comment;
    private Boolean update;
    private int replies;

    public CommentParentDto(BoardComment comment) {
        this.id = comment.getId();
        this.userId = comment.getUserId();
        this.comment = comment.getComment();
        this.update = comment.getUpdate();
        this.replies = CollectionUtils.isEmpty(comment.getReplies()) ? 0 : comment.getReplies().size();
    }
}
