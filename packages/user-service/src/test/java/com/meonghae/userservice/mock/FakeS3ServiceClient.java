package com.meonghae.userservice.mock;

import com.meonghae.userservice.dto.file.S3Request;
import com.meonghae.userservice.dto.file.S3Response;
import com.meonghae.userservice.dto.file.S3Update;
import com.meonghae.userservice.service.client.feign.S3ServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FakeS3ServiceClient implements S3ServiceClient {

    @Override
    public S3Response viewUserFile(String email) {
        return null;
    }

    @Override
    public ResponseEntity<String> uploadFileForUser(MultipartFile file, S3Request data) {
        return null;
    }

    @Override
    public ResponseEntity<String> updateImage(List<MultipartFile> files, List<S3Update> dataList) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteFileForUser(S3Request requestDto) {
        return null;
    }
}
