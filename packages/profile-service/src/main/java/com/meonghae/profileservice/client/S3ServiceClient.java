package com.meonghae.profileservice.client;

import com.meonghae.profileservice.config.FeignHeaderConfig;
import com.meonghae.profileservice.config.FeignUploadConfig;
import com.meonghae.profileservice.dto.S3.S3RequestDto;
import com.meonghae.profileservice.dto.S3.S3ResponseDto;
import com.meonghae.profileservice.dto.S3.S3UpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "s3-file-service",configuration = {FeignHeaderConfig.class, FeignUploadConfig.class})
public interface S3ServiceClient {
  @GetMapping("/files")
  List<S3ResponseDto> getImages(S3RequestDto requestDto);

  @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<String> uploadImages(@RequestPart(value = "files", name = "files") List<MultipartFile> files,
                                      @RequestPart(value = "data", name = "data") S3RequestDto data);

  @GetMapping("/files/pets")
  S3ResponseDto viewPetFile(@SpringQueryMap S3RequestDto requestDto);

  @PutMapping("/files")
  ResponseEntity<String> updateFiles(@RequestPart List<MultipartFile> files, @RequestPart List<S3UpdateDto> requestDto);
}
