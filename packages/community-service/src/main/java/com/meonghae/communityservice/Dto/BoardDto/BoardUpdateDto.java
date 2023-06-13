package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Dto.S3Dto.S3UpdateDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BoardUpdateDto {
    @ApiModelProperty("게시글 제목")
    private String title;
    @ApiModelProperty("게시글 내용")
    private String content;
    @ApiModelProperty("이미지 파일")
    private List<MultipartFile> images;
    @ApiModelProperty("원래 이미지 파일 수정 정보")
    private List<S3UpdateDto> updateDto;
}
