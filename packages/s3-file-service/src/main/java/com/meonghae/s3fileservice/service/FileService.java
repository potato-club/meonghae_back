package com.meonghae.s3fileservice.service;

import com.meonghae.s3fileservice.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    void uploadImages(List<MultipartFile> files, FileRequest requestDto) throws IOException;

    void uploadFileForUser(MultipartFile file, FileUser userDto) throws IOException;

    void updateFiles(List<MultipartFile> files, List<FileUpdate> requestDto) throws IOException;

    List<FileResponse> viewFileList(FileRequest requestDto);

    FileUserResponse viewUserProfile(String email);

    FileUserResponse viewPetProfile(FileRequest requestDto);

    void deleteFiles(FileRequest requestDto);

    void deleteFileForUser(FileUser userDto);

    byte[] downloadImage(String key) throws IOException;
}
