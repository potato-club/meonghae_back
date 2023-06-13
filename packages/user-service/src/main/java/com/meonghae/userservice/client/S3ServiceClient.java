package com.meonghae.userservice.client;

import com.meonghae.userservice.dto.S3Dto.S3RequestDto;
import com.meonghae.userservice.dto.S3Dto.S3ResponseDto;
import com.meonghae.userservice.dto.S3Dto.S3UpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "s3-file-service")
public interface S3ServiceClient {

    @GetMapping("/files/users")
    S3ResponseDto viewUserFile(@RequestParam String email);

    @PostMapping(value = "/files/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadFileForUser(@RequestPart(value = "file", name = "file") MultipartFile file,
                                       @RequestPart(value = "data", name = "data") S3RequestDto data);

    @PutMapping("/files")
    ResponseEntity<String> updateImage(@RequestPart List<MultipartFile> images,
                                       @RequestPart List<S3UpdateDto> requestDto);

    @DeleteMapping("/files/users")
    ResponseEntity<String> deleteFileForUser(@RequestBody S3RequestDto requestDto);
}
