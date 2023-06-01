package com.meonghae.s3fileservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.meonghae.s3fileservice.dto.FileRequestDto;
import com.meonghae.s3fileservice.dto.FileResponseDto;
import com.meonghae.s3fileservice.dto.FileUpdateDto;
import com.meonghae.s3fileservice.dto.FileUserResponseDto;
import com.meonghae.s3fileservice.entity.File;
import com.meonghae.s3fileservice.entity.QFile;
import com.meonghae.s3fileservice.enums.EntityType;
import com.meonghae.s3fileservice.repository.FileRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AmazonS3 s3Client;
    private final FileRepository fileRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public void uploadImages(List<MultipartFile> files, FileRequestDto requestDto) throws IOException {
        List<File> list = this.existsFiles(files);

        for (File file : list) {
            file.setEntityData(requestDto);
            fileRepository.save(file);
        }
    }

    @Override
    public void updateFiles(List<MultipartFile> files, List<FileUpdateDto> updateDto) throws IOException {
        List<File> fileList = null;

        for (FileUpdateDto dto : updateDto) {
            if (dto.getEntityType().equals(EntityType.USER)) {
                fileList = fileRepository.findByEntityTypeAndEmail(dto.getEntityType(), dto.getEmail());
            } else {
                fileList = fileRepository.findByEntityTypeAndTypeId(dto.getEntityType(), dto.getEntityId());
            }
        }

        // 기존 파일 리스트와 새로 업로드한 파일 리스트를 비교하여
        // 바뀐 파일만 업로드하고, 더이상 사용하지 않는 기존 파일은 삭제
        List<File> list = this.existsFiles(files);

        for (int i = 0; i < updateDto.size(); i++) {
            if (updateDto.get(i).isDeleted()) {
                s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileList.get(i).getFileName())); // 사용하지 않는 파일 삭제
                fileRepository.delete(fileList.get(i)); // DB에서도 해당 파일 엔티티 삭제
            }
        }

        for (File file : list) {
            file.update(updateDto.get(0));  // 새로 추가된 파일에 엔티티 정보 추가
            fileRepository.save(file);  // 새 파일 DB 저장
        }
    }

    @Override
    public List<FileResponseDto> viewFileList(FileRequestDto requestDto) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                FileResponseDto.class,
                                QFile.file.fileName,
                                QFile.file.fileUrl,
                                QFile.file.entityType,
                                QFile.file.typeId
                        )
                )
                .from(QFile.file)
                .where(QFile.file.entityType.eq(requestDto.getEntityType())
                        .and(QFile.file.typeId.eq(requestDto.getEntityId())))
                .orderBy(QFile.file.id.asc())
                .fetch();
    }

    @Override
    public FileUserResponseDto viewUserProfile(String email) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                FileUserResponseDto.class,
                                QFile.file.fileName,
                                QFile.file.fileUrl,
                                QFile.file.entityType,
                                QFile.file.email
                        )
                )
                .from(QFile.file)
                .where(QFile.file.email.eq(email))
                .fetchOne();
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