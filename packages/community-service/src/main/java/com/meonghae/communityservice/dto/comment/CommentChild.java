package com.meonghae.communityservice.dto.comment;

import com.meonghae.communityservice.domain.board.BoardComment;
import com.meonghae.communityservice.infra.board.comment.CommentEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentChild {
    @ApiModelProperty("자식댓글 id")
    private Long id;
    @ApiModelProperty("댓글 작성자 프로필 사진")
    private String profileUrl;
    @ApiModelProperty("댓글 작성자가 원글 작성자인지 여부")
    private Boolean isWriter;
    @ApiModelProperty("댓글 내용")
    private String comment;
    @ApiModelProperty("댓글 수정 여부")
    private Boolean update;
    @ApiModelProperty("댓글의 부모댓글 id")
    private Long parentId;
    @ApiModelProperty("댓글 작성 시간")
    private LocalDateTime date;

    public CommentChild(BoardComment child, String url, boolean isWriter) {
        this.id = child.getId();
        this.comment = child.getComment();
        this.isWriter = isWriter;
        this.profileUrl = url;
        this.update = child.getUpdated();
        this.parentId = child.getParent().getId();
        this.date = child.getCreatedDate();
    }
}
