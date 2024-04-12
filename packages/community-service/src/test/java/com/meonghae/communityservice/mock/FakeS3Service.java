package com.meonghae.communityservice.mock;

import com.meonghae.communityservice.application.port.S3ServicePort;
import com.meonghae.communityservice.dto.s3.S3Request;
import com.meonghae.communityservice.dto.s3.S3Response;
import com.meonghae.communityservice.dto.s3.S3Update;
import com.meonghae.communityservice.dto.s3.UserImage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FakeS3Service implements S3ServicePort {

    private final List<S3Response> list = new ArrayList<>();

    @Override
    public List<S3Response> getImages(S3Request requestDto) {
        return list.stream().filter(item -> item.getEntityType().equals(requestDto.getEntityType()) &&
                item.getTypeId().equals(requestDto.getEntityId())).collect(Collectors.toList());
    }

    @Override
    public UserImage getUserImage(String email) {
        return null;
    }

    @Override
    public ResponseEntity<String> uploadImage(List<MultipartFile> files, S3Request data) {
        for (MultipartFile file : files) {
            S3Response response = S3Response.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl("testUrl")
                    .entityType(data.getEntityType())
                    .typeId(data.getEntityId())
                    .build();

            list.add(response);
        }

        return ResponseEntity.ok("ok");
    }

    @Override
    public ResponseEntity<String> updateImage(List<MultipartFile> files, List<S3Update> dataList) {
        Long id = dataList.get(0).getEntityId();
        String type = dataList.get(0).getEntityType();

       list.removeIf(item -> item.getTypeId().equals(id)
               && item.getEntityType().equals(type));

        for (MultipartFile file : files) {
            S3Response response = S3Response.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl("testUrl - " + file.getOriginalFilename())
                    .entityType(type)
                    .typeId(id)
                    .build();

            list.add(response);
        }

        return ResponseEntity.ok("ok");
    }

    @Override
    public ResponseEntity<String> deleteImage(S3Request requestDto) {
        list.removeIf(item -> item.getTypeId().equals(requestDto.getEntityId())
                && item.getEntityType().equals(requestDto.getEntityType()));

        return ResponseEntity.ok("ok");
    }
}
