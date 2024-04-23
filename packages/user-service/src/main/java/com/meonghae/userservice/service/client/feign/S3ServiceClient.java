package com.meonghae.userservice.service.client.feign;

import com.meonghae.userservice.dto.file.S3Request;
import com.meonghae.userservice.dto.file.S3Response;
import com.meonghae.userservice.dto.file.S3Update;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "s3-file-service")
public interface S3ServiceClient {

    @GetMapping("/files/users")
    S3Response viewUserFile(@RequestParam String email);

    @PostMapping(value = "/files/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadFileForUser(@RequestPart(value = "file", name = "file") MultipartFile file,
                                       @RequestPart(value = "data", name = "data") S3Request data);

    @PutMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> updateImage(@RequestPart(value = "files", name = "files") List<MultipartFile> files,
                                       @RequestPart(value = "dataList", name = "dataList") List<S3Update> dataList);

    @DeleteMapping("/files/users")
    ResponseEntity<String> deleteFileForUser(@RequestBody S3Request requestDto);
}
