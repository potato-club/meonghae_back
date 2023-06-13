package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Dto.S3Dto.ImageListDto;
import com.meonghae.communityservice.Dto.S3Dto.S3ResponseDto;
import com.meonghae.communityservice.Entity.Board.Board;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BoardDetailDto {
    @ApiModelProperty("게시글 id")
    private Long id;
    @ApiModelProperty("게시글 작성자 프로필 사진")
    private String profileUrl;
    @ApiModelProperty("게시글 제목")
    private String title;
    @ApiModelProperty("게시글 내용")
    private String content;
    @ApiModelProperty("게시글 이미지 리스트")
    private List<ImageListDto> images;
    @ApiModelProperty("게시글 댓글 총 개수")
    private int commentSize;
    @ApiModelProperty("게시글 좋아요 개수")
    private int likes;

    public BoardDetailDto(Board board, String url) {
        this.id = board.getId();
        this.profileUrl = url;
        this.title = board.getTitle();
        this.content = board.getContent();
        this.commentSize = board.getComments().isEmpty() ? 0 : board.getComments().size();
        this.likes = board.getLikes();
    }

    public void setImages(List<S3ResponseDto> imageList) {
        this.images = imageList.stream().map(ImageListDto::new).collect(Collectors.toList());
    }
}
