package com.meonghae.profileservice.client;

import com.meonghae.profileservice.config.FeignHeaderConfig;
import com.meonghae.profileservice.dto.S3.S3RequestDto;
import com.meonghae.profileservice.dto.S3.S3ResponseDto;
import com.meonghae.profileservice.dto.S3.S3UpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "s3-file-service",configuration = {FeignHeaderConfig.class})
public interface S3ServiceClient {
  @GetMapping("/files")
  List<S3ResponseDto> getImages(S3RequestDto requestDto);

  @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<String> uploadImages(@RequestPart List<MultipartFile> images, @RequestPart S3RequestDto requestDto);

  @GetMapping("/files/pets")
  S3ResponseDto viewPetFile(S3RequestDto requestDto);

  @PutMapping("/files")
  ResponseEntity<String> updateFiles(@RequestPart List<MultipartFile> files, @RequestPart List<S3UpdateDto> requestDto);
}
