package com.meonghae.communityservice.mock;

import com.meonghae.communityservice.application.port.S3ServicePort;
import com.meonghae.communityservice.dto.s3.S3Request;
import com.meonghae.communityservice.dto.s3.S3Response;
import com.meonghae.communityservice.dto.s3.S3Update;
import com.meonghae.communityservice.dto.s3.UserImage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FakeS3Service implements S3ServicePort {
    @Override
    public List<S3Response> getImages(S3Request requestDto) {
        return null;
    }

    @Override
    public UserImage getUserImage(String email) {
        return null;
    }

    @Override
    public ResponseEntity<String> uploadImage(List<MultipartFile> files, S3Request data) {
        return ResponseEntity.ok("ok");
    }

    @Override
    public ResponseEntity<String> updateImage(List<MultipartFile> files, List<S3Update> dataList) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteImage(S3Request requestDto) {
        return null;
    }
}
