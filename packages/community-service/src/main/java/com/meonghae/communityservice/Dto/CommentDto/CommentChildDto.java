package com.meonghae.communityservice.Dto.CommentDto;

import com.meonghae.communityservice.Entity.Board.BoardComment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentChildDto {
    @ApiModelProperty("자식댓글 id")
    private Long id;
    @ApiModelProperty("댓글 작성자 닉네임")
    private String nickname;
    @ApiModelProperty("댓글 내용")
    private String comment;
    @ApiModelProperty("댓글 수정 여부")
    private Boolean update;
    @ApiModelProperty("댓글의 부모댓글 id")
    private Long parentId;

    public CommentChildDto(BoardComment child, String nickname) {
        this.id = child.getId();
        this.comment = child.getComment();
        this.nickname = nickname;
        this.update = child.getUpdated();
        this.parentId = child.getParent().getId();
    }
}
