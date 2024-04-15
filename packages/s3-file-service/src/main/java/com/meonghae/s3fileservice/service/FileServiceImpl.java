package com.meonghae.s3fileservice.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.meonghae.s3fileservice.domain.File;
import com.meonghae.s3fileservice.dto.*;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import com.meonghae.s3fileservice.service.port.FileRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Builder
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService {

    private final AmazonS3Client s3Client;
    private final FileRepository fileRepository;
    private String bucketName;

    public FileServiceImpl(AmazonS3Client amazonS3Client, FileRepository fileRepository,
                           @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.s3Client = amazonS3Client;
        this.fileRepository = fileRepository;
        this.bucketName = bucketName;
    }

    @Override
    @Transactional
    public void uploadImages(List<MultipartFile> files, FileRequest request) throws IOException {
        List<File> list = this.existsFiles(files);

        for (File file : list) {
            file.uploadForData(request);
            fileRepository.save(file);
        }
    }

    @Override
    @Transactional
    public void uploadFileForUser(MultipartFile file, FileUser user) throws IOException {
        List<MultipartFile> files = new ArrayList<>();
        files.add(file);

        List<File> list = this.existsFiles(files);
        for (File image : list) {
            image.uploadForUser(user);
            fileRepository.save(image);
        }
    }

    @Override
    @Transactional
    public void updateFiles(List<MultipartFile> files, List<FileUpdate> update) throws IOException {
        // 원래 데이터 중 하나만 있어도 리스트 조회 가능
        FileUpdate dto = update.get(0);

        List<File> fileList;

        if (dto.getEntityType().equals(EntityType.USER)) {
            fileList = fileRepository.findByEntityTypeAndEmail(dto.getEntityType(), dto.getEmail());
        } else {
            fileList = fileRepository.findByEntityTypeAndTypeId(dto.getEntityType(), dto.getEntityId());
        }


        for (int i = 0; i < update.size(); i++) {
            if (update.get(i).isDeleted()) {
                s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileList.get(i).getFileName())); // 사용하지 않는 파일 삭제
                fileRepository.delete(fileList.get(i)); // DB에서도 해당 파일 엔티티 삭제
            }
        }

        // 기존 파일 리스트와 새로 업로드한 파일 리스트를 비교하여
        // 바뀐 파일만 업로드하고, 더이상 사용하지 않는 기존 파일은 삭제
        List<File> list = this.existsFiles(files);

        for (File file : list) {
            file.update(update.get(0));  // 새로 추가된 파일에 엔티티 정보 추가
            fileRepository.save(file);  // 새 파일 DB 저장
        }
    }

    @Override
    public List<FileResponse> viewFileList(FileRequest request) {
        return fileRepository.getFileList(request);
    }

    @Override
    public FileUserResponse viewUserProfile(String email) {
        return fileRepository.getUserProfile(email);
    }

    @Override
    public FileUserResponse viewPetProfile(FileRequest request) {
        return fileRepository.getPetProfile(request);
    }

    @Override
    @Transactional
    public void deleteFiles(FileRequest request) {
        List<File> files = fileRepository.findByEntityTypeAndTypeId(request.getEntityType(), request.getEntityId());
        for (File file : files) {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, file.getFileName()));
            fileRepository.delete(file);
        }
    }

    @Override
    @Transactional
    public void deleteFileForUser(FileUser user) {
        List<File> files = fileRepository.findByEntityTypeAndEmail(user.getEntityType(), user.getEmail());
        for (File file : files) {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, file.getFileName()));
            fileRepository.delete(file);
        }
    }

    @Override
    public byte[] downloadImage(String key) throws IOException {
        byte[] content;
        final S3Object s3Object = s3Client.getObject(bucketName, key);
        final S3ObjectInputStream stream = s3Object.getObjectContent();
        try {
            content = IOUtils.toByteArray(stream);
            s3Object.close();
        } catch(final IOException ex) {
            throw new IOException("IO Error Message= " + ex.getMessage());
        }
        return content;
    }

    private List<File> existsFiles(List<MultipartFile> files) throws IOException {
        List<File> list = new ArrayList<>();

        for (MultipartFile file : files) {
            String key = file.getOriginalFilename();
            if (s3Client.doesObjectExist(bucketName, key)) {
                continue;
            }
            String fileName = UUID.randomUUID() + "-" + key;
            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
            File image = File.builder()
                    .fileName(fileName)
                    .fileUrl(s3Client.getUrl(bucketName, fileName).toString())
                    .build();
            list.add(image);
        }

        return list;
    }
}
