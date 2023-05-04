package com.meonghae.communityservice.Dto.CommentDto;

import com.meonghae.communityservice.Entity.Board.BoardComment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

@Getter
@NoArgsConstructor
public class CommentParentDto {
    @ApiModelProperty("부모댓글 id")
    private Long id;
    @ApiModelProperty("댓글 작성자 닉네임")
    private String nickname;
    @ApiModelProperty("댓글 내용")
    private String comment;
    @ApiModelProperty("댓글 수정 여부")
    private Boolean update;
    @ApiModelProperty("대댓글 개수")
    private int replies;

    public CommentParentDto(BoardComment comment, String nickname) {
        this.id = comment.getId();
        this.nickname = nickname;
        this.comment = comment.getComment();
        this.update = comment.getUpdated();
        this.replies = CollectionUtils.isEmpty(comment.getReplies()) ? 0 : comment.getReplies().size();
    }
}
