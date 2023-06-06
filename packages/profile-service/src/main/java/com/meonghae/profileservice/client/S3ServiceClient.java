package com.meonghae.profileservice.client;

import com.meonghae.profileservice.config.FeignHeaderConfig;
import com.meonghae.profileservice.dto.S3.S3RequestDto;
import com.meonghae.profileservice.dto.S3.S3ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "s3-file-service",configuration = {FeignHeaderConfig.class})
public interface S3ServiceClient {
  @GetMapping("/files")
  List<S3ResponseDto> getImages(S3RequestDto requestDto);

  @PostMapping("/files")
  ResponseEntity<String> uploadImage(@RequestPart List<MultipartFile> images, @RequestPart S3RequestDto requestDto);
}
