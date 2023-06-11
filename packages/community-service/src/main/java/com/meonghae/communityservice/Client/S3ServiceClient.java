package com.meonghae.communityservice.Client;

import com.meonghae.communityservice.Config.FeignHeaderConfig;
import com.meonghae.communityservice.Config.FeignUploadConfig;
import com.meonghae.communityservice.Dto.S3Dto.S3RequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3ResponseDto;
import com.meonghae.communityservice.Dto.S3Dto.S3UpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "s3-file-service", configuration = {FeignHeaderConfig.class, FeignUploadConfig.class})
public interface S3ServiceClient {
    @GetMapping("/files")
    List<S3ResponseDto> getImages(S3RequestDto requestDto);

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadImage(@RequestPart(value = "files") List<MultipartFile> files,
                                       @RequestPart(value = "data") S3RequestDto data);

    @PutMapping("/files")
    ResponseEntity<String> updateImage(@RequestPart List<MultipartFile> images,
                                       @RequestPart List<S3UpdateDto> requestDto);
}
