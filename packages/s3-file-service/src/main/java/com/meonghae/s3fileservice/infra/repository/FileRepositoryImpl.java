package com.meonghae.s3fileservice.infra.repository;

import com.meonghae.s3fileservice.domain.File;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import com.meonghae.s3fileservice.dto.FileRequest;
import com.meonghae.s3fileservice.dto.FileResponse;
import com.meonghae.s3fileservice.dto.FileUserResponse;
import com.meonghae.s3fileservice.entity.QFile;
import com.meonghae.s3fileservice.infra.entity.FileEntity;
import com.meonghae.s3fileservice.infra.entity.QFileEntity;
import com.meonghae.s3fileservice.service.port.FileRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

    private final FileJpaRepository fileRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<File> findByEntityTypeAndTypeId(EntityType entityType, Long typeId) {
        return fileRepository.findByEntityTypeAndTypeId(entityType, typeId)
                .stream()
                .map(FileEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<File> findByEntityTypeAndEmail(EntityType entityType, String email) {
        return fileRepository.findByEntityTypeAndEmail(entityType, email)
                .stream()
                .map(FileEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void save(File file) {
        fileRepository.save(FileEntity.from(file));
    }

    @Override
    public void delete(File file) {
        fileRepository.delete(FileEntity.from(file));
    }

    @Override
    public List<FileResponse> getFileList(FileRequest request) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                FileResponse.class,
                                QFileEntity.fileEntity.fileName,
                                QFileEntity.fileEntity.fileUrl,
                                QFileEntity.fileEntity.entityType,
                                QFileEntity.fileEntity.typeId
                        )
                )
                .from(QFileEntity.fileEntity)
                .where(QFileEntity.fileEntity.entityType.eq(request.getEntityType())
                        .and(QFileEntity.fileEntity.typeId.eq(request.getEntityId())))
                .orderBy(QFileEntity.fileEntity.id.asc())
                .fetch();
    }

    @Override
    public FileUserResponse getUserProfile(String email) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                FileUserResponse.class,
                                QFileEntity.fileEntity.fileName,
                                QFileEntity.fileEntity.fileUrl,
                                QFileEntity.fileEntity.entityType,
                                QFileEntity.fileEntity.email
                        )
                )
                .from(QFileEntity.fileEntity)
                .where(QFileEntity.fileEntity.email.eq(email))
                .fetchOne();
    }

    @Override
    public FileUserResponse getPetProfile(FileRequest request) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                FileUserResponse.class,
                                QFileEntity.fileEntity.fileName,
                                QFileEntity.fileEntity.fileUrl,
                                QFileEntity.fileEntity.entityType,
                                QFileEntity.fileEntity.email
                        )
                )
                .from(QFileEntity.fileEntity)
                .where(QFileEntity.fileEntity.entityType.eq(request.getEntityType())
                        .and(QFileEntity.fileEntity.typeId.eq(request.getEntityId())))
                .fetchOne();
    }

    @Override
    public boolean existsByEntityTypeAndEmail(EntityType entityType, String email) {
        return fileRepository.existsByEntityTypeAndEmail(entityType, email);
    }

    @Override
    public boolean existsByEntityTypeAndTypeId(EntityType entityType, Long id) {
        return fileRepository.existsByEntityTypeAndTypeId(entityType, id);
    }
}
