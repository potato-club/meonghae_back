package com.meonghae.s3fileservice.service;

import com.meonghae.s3fileservice.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    void uploadImages(List<MultipartFile> files, FileRequestDto requestDto) throws IOException;

    void uploadFileForUser(MultipartFile file, FileUserDto userDto) throws IOException;

    void updateFiles(List<MultipartFile> files, List<FileUpdateDto> requestDto) throws IOException;

    List<FileResponseDto> viewFileList(FileRequestDto requestDto);

    FileUserResponseDto viewUserProfile(String email);

    FileUserResponseDto viewPetProfile(FileRequestDto requestDto);

    void deleteFiles(FileRequestDto requestDto);

    void deleteFileForUser(FileUserDto userDto);

    byte[] downloadImage(String key) throws IOException;
}
