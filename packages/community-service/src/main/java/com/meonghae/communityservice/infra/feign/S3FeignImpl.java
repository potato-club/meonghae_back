package com.meonghae.communityservice.infra.feign;

import com.meonghae.communityservice.application.port.S3ServicePort;
import com.meonghae.communityservice.dto.s3.S3Request;
import com.meonghae.communityservice.dto.s3.S3Response;
import com.meonghae.communityservice.dto.s3.S3Update;
import com.meonghae.communityservice.dto.s3.UserImage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class S3FeignImpl implements S3ServicePort {

    private final S3ServiceClient serviceClient;

    @Override
    public List<S3Response> getImages(S3Request requestDto) {
        return serviceClient.getImages(requestDto);
    }

    @Override
    public UserImage getUserImage(String email) {
        return serviceClient.getUserImage(email);
    }

    @Override
    public ResponseEntity<String> uploadImage(List<MultipartFile> files, S3Request data) {
        return serviceClient.uploadImage(files, data);
    }

    @Override
    public ResponseEntity<String> updateImage(List<MultipartFile> files, List<S3Update> dataList) {
        return serviceClient.updateImage(files, dataList);
    }

    @Override
    public ResponseEntity<String> deleteImage(S3Request requestDto) {
        return serviceClient.deleteImage(requestDto);
    }
}
