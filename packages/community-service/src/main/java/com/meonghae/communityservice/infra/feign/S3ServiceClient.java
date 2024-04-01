package com.meonghae.communityservice.infra.feign;

import com.meonghae.communityservice.core.FeignHeaderConfig;
import com.meonghae.communityservice.core.FeignUploadConfig;
import com.meonghae.communityservice.dto.s3.S3Request;
import com.meonghae.communityservice.dto.s3.S3Response;
import com.meonghae.communityservice.dto.s3.S3Update;
import com.meonghae.communityservice.dto.s3.UserImage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "s3-file-service", configuration = {FeignHeaderConfig.class, FeignUploadConfig.class})
public interface S3ServiceClient {
    @GetMapping("/files")
    List<S3Response> getImages(@SpringQueryMap S3Request requestDto);

    @GetMapping("/files/users")
    UserImage getUserImage(@RequestParam String email);

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadImage(@RequestPart(value = "files", name = "files") List<MultipartFile> files,
                                       @RequestPart(value = "data", name = "data") S3Request data);

    @PutMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> updateImage(@RequestPart(value = "files", name = "files") List<MultipartFile> files,
                                       @RequestPart(value = "dataList", name = "dataList") List<S3Update> dataList);

    @DeleteMapping("/files")
    ResponseEntity<String> deleteImage(@RequestBody S3Request requestDto);
}
