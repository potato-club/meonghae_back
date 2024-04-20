package com.meonghae.communityservice.application.port;

import com.meonghae.communityservice.dto.s3.S3RequestDto;
import com.meonghae.communityservice.dto.s3.S3ResponseDto;
import com.meonghae.communityservice.dto.s3.S3UpdateDto;
import com.meonghae.communityservice.dto.s3.UserImageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3ServicePort {
    List<S3ResponseDto> getImages(S3RequestDto requestDto);

    UserImageDto getUserImage(String email);

    ResponseEntity<String> uploadImage(List<MultipartFile> files, S3RequestDto data);

    ResponseEntity<String> updateImage(List<MultipartFile> files, List<S3UpdateDto> dataList);

    ResponseEntity<String> deleteImage(S3RequestDto requestDto);
}
