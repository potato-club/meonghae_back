package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Enum.BoardType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardMainDto {
    @ApiModelProperty("게시글 id")
    private Long id;
    @ApiModelProperty("게시글 제목")
    private String title;
    @ApiModelProperty("게시판 Type")
    private BoardType type;
    @ApiModelProperty("게시글 좋아요 개수")
    private int likes;
    @ApiModelProperty("게시글 총 댓글 개수")
    private int commentSize;
    @ApiModelProperty("게시글 내 이미지 여부")
    private boolean image;

    public BoardMainDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.type = board.getType();
        this.commentSize = board.getComments().isEmpty() ? 0 : board.getComments().size();
        this.likes = board.getLikes();
        this.image = board.getHasImage();
    }
}
