package com.meonghae.s3fileservice.infra.entity;

import com.meonghae.s3fileservice.domain.File;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class FileEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column
    private EntityType entityType;

    @Column
    private Long typeId;

    @Column
    private String email;

    public static FileEntity from(File file) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.id = file.getId();
        fileEntity.fileName = file.getFileName();
        fileEntity.fileUrl = file.getFileUrl();
        fileEntity.entityType = file.getEntityType();
        fileEntity.typeId = file.getTypeId();
        fileEntity.email = file.getEmail();

        return fileEntity;
    }

    public File toModel() {
        return File.builder()
                .id(id)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .entityType(entityType)
                .typeId(typeId)
                .email(email)
                .build();
    }
}
