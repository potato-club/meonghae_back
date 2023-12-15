package com.meonghae.communityservice.Client;

import com.meonghae.communityservice.Config.FeignHeaderConfig;
import com.meonghae.communityservice.Config.FeignUploadConfig;
import com.meonghae.communityservice.Dto.S3Dto.S3RequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3ResponseDto;
import com.meonghae.communityservice.Dto.S3Dto.S3UpdateDto;
import com.meonghae.communityservice.Dto.S3Dto.UserImageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "s3-file-service", configuration = {FeignHeaderConfig.class, FeignUploadConfig.class})
public interface S3ServiceClient {
    @GetMapping("/files")
    List<S3ResponseDto> getImages(S3RequestDto requestDto);

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
