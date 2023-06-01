package com.meonghae.s3fileservice.service;

import com.meonghae.s3fileservice.dto.FileRequestDto;
import com.meonghae.s3fileservice.dto.FileResponseDto;
import com.meonghae.s3fileservice.dto.FileUpdateDto;
import com.meonghae.s3fileservice.dto.FileUserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    void uploadImages(List<MultipartFile> files, FileRequestDto requestDto) throws IOException;

    void updateFiles(List<MultipartFile> files, List<FileUpdateDto> requestDto) throws IOException;

    List<FileResponseDto> viewFileList(FileRequestDto requestDto);

    FileUserResponseDto viewUserProfile(String email);

    byte[] downloadImage(String key) throws IOException;
}
