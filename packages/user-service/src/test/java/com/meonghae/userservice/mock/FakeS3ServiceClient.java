package com.meonghae.userservice.mock;

import com.meonghae.userservice.core.exception.ErrorCode;
import com.meonghae.userservice.core.exception.impl.UnAuthorizedException;
import com.meonghae.userservice.domin.file.enums.FileEnum;
import com.meonghae.userservice.dto.file.S3Request;
import com.meonghae.userservice.dto.file.S3Response;
import com.meonghae.userservice.dto.file.S3Update;
import com.meonghae.userservice.service.client.feign.S3ServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public class FakeS3ServiceClient implements S3ServiceClient {

    private final List<S3Response> list = Collections.synchronizedList(new ArrayList<>());

    @Override
    public S3Response viewUserFile(String email) {
        Optional<S3Response> response = list.stream().filter(item -> item.getEmail().equals(email)).findAny();
        return response.orElse(null);
    }

    @Override
    public ResponseEntity<String> uploadFileForUser(MultipartFile file, S3Request data) {

        if (!data.getEntityType().equals("USER")) {
            throw new UnAuthorizedException("접근 권한 없음", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        S3Response response = S3Response.builder()
                .fileName(UUID.randomUUID() + "-" + file.getOriginalFilename())
                .fileUrl(UUID.randomUUID() + "-" + "test")
                .entityType(FileEnum.USER)
                .email(data.getEmail())
                .build();

        list.add(response);

        return ResponseEntity.ok("업로드 성공");
    }

    @Override
    public ResponseEntity<String> updateImage(List<MultipartFile> files, List<S3Update> dataList) {

        if (files.size() != 1) {
            throw new UnAuthorizedException("사진은 최대 1장까지만 가능함.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        list.removeIf(item -> item.getEmail().equals(dataList.get(0).getEmail()));

        S3Response response = S3Response.builder()
                .fileName(UUID.randomUUID() + "-" + files.get(0).getOriginalFilename())
                .fileUrl(UUID.randomUUID() + "-" + "test")
                .entityType(FileEnum.USER)
                .email(dataList.get(0).getEmail())
                .build();

        list.add(response);

        return ResponseEntity.ok("업데이트 성공");
    }

    @Override
    public ResponseEntity<String> deleteFileForUser(S3Request requestDto) {
        list.removeIf(item -> item.getEmail().equals(requestDto.getEmail()));
        return ResponseEntity.ok("삭제 성공");
    }
}
