package com.meonghae.s3fileservice.web;

import com.meonghae.s3fileservice.dto.*;
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
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/files")
@Api(tags = {"File Upload & Download Controller"})
public class FileController {

    private final FileService fileService;

    @Operation(summary = "File Upload API")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImages(@RequestPart(value = "files") List<MultipartFile> files,
                                               @RequestPart(value = "data") FileRequest data) throws IOException {
        fileService.uploadImages(files, data);
        return ResponseEntity.ok("Upload Success");
    }

    @Operation(summary = "File Upload For User API")
    @PostMapping(value = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImagesForUser(@RequestPart(value = "file") MultipartFile file,
                                               @RequestPart(value = "data") FileUser data) throws IOException {
        fileService.uploadFileForUser(file, data);
        return ResponseEntity.ok("Upload Success");
    }

    @Operation(summary = "File Update API")
    @PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateFiles(@RequestPart(value = "files") List<MultipartFile> files,
                                               @RequestPart(value = "dataList") List<FileUpdate> dataList) throws IOException {
        fileService.updateFiles(files, dataList);
        return ResponseEntity.ok("Update Success");
    }

    @Operation(summary = "View File List API")
    @GetMapping(value = "")
    public List<FileResponse> viewFileList(@ModelAttribute FileRequest requestDto) {
        return fileService.viewFileList(requestDto);
    }

    @Operation(summary = "View File about User Entity API")
    @GetMapping("/users")
    public FileUserResponse viewUserFile(@RequestParam String email) {
        return fileService.viewUserProfile(email);
    }

    @Operation(summary = "View File about Pet Entity Api")
    @GetMapping ("/pets")
    public FileUserResponse viewPetFile(@ModelAttribute FileRequest requestDto){
        return fileService.viewPetProfile(requestDto);
    }

    @Operation(summary = "파일 삭제 API")
    @DeleteMapping ("")
    public ResponseEntity<String> deleteFiles(@RequestBody FileRequest requestDto){
        fileService.deleteFiles(requestDto);
        return ResponseEntity.ok("File Delete Success");
    }

    @Operation(summary = "유저 서비스 전용 파일 삭제 API")
    @DeleteMapping ("/users")
    public ResponseEntity<String> deleteFileForUser(@RequestBody FileUser userDto){
        fileService.deleteFileForUser(userDto);
        return ResponseEntity.ok("File Delete Success");
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
                            URLEncoder.encode(key, StandardCharsets.UTF_8))
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().contentLength(0).body(null);
        }
    }
}
