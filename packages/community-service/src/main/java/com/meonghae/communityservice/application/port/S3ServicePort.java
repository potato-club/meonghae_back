package com.meonghae.communityservice.application.port;

import com.meonghae.communityservice.dto.s3.S3Request;
import com.meonghae.communityservice.dto.s3.S3Response;
import com.meonghae.communityservice.dto.s3.S3Update;
import com.meonghae.communityservice.dto.s3.UserImage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3ServicePort {
    List<S3Response> getImages(S3Request requestDto);

    UserImage getUserImage(String email);

    ResponseEntity<String> uploadImage(List<MultipartFile> files, S3Request data);

    ResponseEntity<String> updateImage(List<MultipartFile> files, List<S3Update> dataList);

    ResponseEntity<String> deleteImage(S3Request requestDto);
}
