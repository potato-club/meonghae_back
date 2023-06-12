package com.meonghae.userservice.client;

import com.meonghae.userservice.dto.S3Dto.S3RequestDto;
import com.meonghae.userservice.dto.S3Dto.S3ResponseDto;
import com.meonghae.userservice.dto.S3Dto.S3UpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "s3-file-service")
public interface S3ServiceClient {

    @GetMapping("/files/users")
    S3ResponseDto viewUserFile(String email);

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Error 400
    ResponseEntity<String> uploadImage(@RequestPart(value = "files", name = "files") List<MultipartFile> files,
                                       @RequestPart(value = "data", name = "data") S3RequestDto data);

    @PutMapping("/files")
    ResponseEntity<String> updateImage(@RequestPart List<MultipartFile> images,
                                       @RequestPart List<S3UpdateDto> requestDto);
}
