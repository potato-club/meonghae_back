package com.meonghae.communityservice.dto.board;

import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.infra.board.board.BoardEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardList {
    @ApiModelProperty("게시글 id")
    private Long id;
    @ApiModelProperty("게시글 작성자 프로필 사진")
    private String profileUrl;
    @ApiModelProperty("게시글 제목")
    private String title;
    @ApiModelProperty("게시글 내용")
    private String content;
    @ApiModelProperty("게시글 좋아요 개")
    private int likes;
    @ApiModelProperty("게시글 총 댓글 개수")
    private int commentSize;
    @ApiModelProperty("게시글 내 이미지 여부")
    private boolean hasImage;
    @ApiModelProperty("게시글 작성 시간")
    private LocalDateTime date;

    public BoardList(Board board, String profileUrl) {
        this.id = board.getId();
        this.profileUrl = profileUrl;
        this.title = board.getTitle();
        this.content = board.getContent();
        this.likes = board.getLikes();
        this.commentSize = board.getComments().isEmpty() ? 0 : board.getComments().size();
        this.hasImage = board.getHasImage();
        this.date = board.getCreatedDate();
    }
}
