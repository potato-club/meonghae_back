package com.meonghae.communityservice.infra.feign;

import com.meonghae.communityservice.infra.feign.config.FeignHeaderConfig;
import com.meonghae.communityservice.infra.feign.config.FeignUploadConfig;
import com.meonghae.communityservice.dto.s3.S3RequestDto;
import com.meonghae.communityservice.dto.s3.S3ResponseDto;
import com.meonghae.communityservice.dto.s3.S3UpdateDto;
import com.meonghae.communityservice.dto.s3.UserImageDto;
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
    List<S3ResponseDto> getImages(@SpringQueryMap S3RequestDto requestDto);

    @GetMapping("/files/users")
    UserImageDto getUserImage(@RequestParam String email);

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadImage(@RequestPart(value = "files", name = "files") List<MultipartFile> files,
                                       @RequestPart(value = "data", name = "data") S3RequestDto data);

    @PutMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> updateImage(@RequestPart(value = "files", name = "files") List<MultipartFile> files,
                                       @RequestPart(value = "dataList", name = "dataList") List<S3UpdateDto> dataList);

    @DeleteMapping("/files")
    ResponseEntity<String> deleteImage(@RequestBody S3RequestDto requestDto);
}
