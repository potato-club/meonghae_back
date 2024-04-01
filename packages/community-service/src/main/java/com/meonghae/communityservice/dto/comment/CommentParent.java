package com.meonghae.communityservice.dto.comment;

import com.meonghae.communityservice.domain.board.BoardComment;
import com.meonghae.communityservice.infra.board.comment.CommentEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentParent {
    @ApiModelProperty("부모댓글 id")
    private Long id;
    @ApiModelProperty("댓글 작성자 프로필 사진")
    private String profileUrl;
    @ApiModelProperty("댓글 작성자가 원글 작성자인지 여부")
    private Boolean isWriter;
    @ApiModelProperty("댓글 내용")
    private String comment;
    @ApiModelProperty("댓글 수정 여부")
    private Boolean update;
    @ApiModelProperty("대댓글 개수")
    private int replies;
    @ApiModelProperty("댓글 작성 시간")
    private LocalDateTime date;

    public CommentParent(BoardComment comment, String url, boolean isWriter) {
        this.id = comment.getId();
        this.profileUrl = url;
        this.comment = comment.getComment();
        this.update = comment.getUpdated();
        this.isWriter = isWriter;
        this.replies = CollectionUtils.isEmpty(comment.getReplies()) ? 0 : comment.getReplies().size();
        this.date = comment.getCreatedDate();
    }
}
