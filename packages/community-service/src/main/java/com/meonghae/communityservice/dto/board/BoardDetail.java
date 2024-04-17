package com.meonghae.communityservice.dto.board;

import com.meonghae.communityservice.domain.board.Board;
import com.meonghae.communityservice.dto.s3.ImageList;
import com.meonghae.communityservice.dto.s3.S3Response;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BoardDetail {
    @ApiModelProperty("게시글 id")
    private Long id;
    @ApiModelProperty("게시글 작성자 프로필 사진")
    private String profileUrl;
    @ApiModelProperty("게시글 제목")
    private String title;
    @ApiModelProperty("게시글 내용")
    private String content;
    @ApiModelProperty("게시글 이미지 리스트")
    private List<ImageList> images;
    @ApiModelProperty("게시글 댓글 총 개수")
    private int commentSize;
    @ApiModelProperty("게시글 좋아요 개수")
    private int likes;
    @ApiModelProperty("좋아요 여부")
    private boolean likeStatus;
    @ApiModelProperty("게시글 작성자 여부")
    private boolean writer;
    @ApiModelProperty("게시글 작성 시간")
    private LocalDateTime date;

    public BoardDetail(Board board, String url, boolean status, boolean isWriter, int commentCount) {
        this.id = board.getId();
        this.profileUrl = url;
        this.title = board.getTitle();
        this.content = board.getContent();
        this.commentSize = commentCount;
        this.likes = board.getLikes();
        this.likeStatus = status;
        this.writer = isWriter;
        this.date = board.getCreatedDate();
    }

    public void setImages(List<S3Response> imageList) {
        this.images = imageList.stream().map(ImageList::new).collect(Collectors.toList());
    }
}
