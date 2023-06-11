package com.meonghae.s3fileservice.controller;

import com.meonghae.s3fileservice.dto.FileRequestDto;
import com.meonghae.s3fileservice.dto.FileResponseDto;
import com.meonghae.s3fileservice.dto.FileUpdateDto;
import com.meonghae.s3fileservice.dto.FileUserResponseDto;
import com.meonghae.s3fileservice.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/files")
@Api(tags = {"File Upload & Download Controller"})
public class FileController {

    private final FileService fileService;

    @Operation(summary = "File Upload API")
    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImages(@RequestPart(value = "files") List<MultipartFile> files,
                                               @RequestPart(value = "data") FileRequestDto requestDto) throws IOException {
        fileService.uploadImages(files, requestDto);
        return ResponseEntity.ok("Upload Success");
    }

    @Operation(summary = "File Update API")
    @PutMapping("")
    public ResponseEntity<String> updateFiles(@RequestPart List<MultipartFile> files,
                                               @RequestPart List<FileUpdateDto> requestDto) throws IOException {
        fileService.updateFiles(files, requestDto);
        return ResponseEntity.ok("Update Success");
    }

    @Operation(summary = "View File List API")
    @GetMapping("")
    public List<FileResponseDto> viewFileList(FileRequestDto requestDto) {
        return fileService.viewFileList(requestDto);
    }

    @Operation(summary = "View File about User Entity API")
    @GetMapping("/users")
    public FileUserResponseDto viewUserFile(String email) {
        return fileService.viewUserProfile(email);
    }

    @Operation(summary = "View File about Pet Entity Api")
    @GetMapping ("/pets")
    public FileUserResponseDto viewPetFile(FileRequestDto requestDto){
        return fileService.viewPetProfile(requestDto);
    }

    @Operation(summary = "File Download API")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> s3Download(@RequestParam String key) {
        try {
            byte[] data = fileService.downloadImage(key);
            InputStream inputStream = new ByteArrayInputStream(data);
            InputStreamResource resource = new InputStreamResource(inputStream);
            return ResponseEntity.ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-Disposition", "attachment; filename=" +
                            URLEncoder.encode(key, "UTF-8"))
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().contentLength(0).body(null);
        }
    }
}
