package com.meonghae.s3fileservice.entity;

import com.meonghae.s3fileservice.dto.FileRequestDto;
import com.meonghae.s3fileservice.dto.FileUpdateDto;
import com.meonghae.s3fileservice.dto.FileUserDto;
import com.meonghae.s3fileservice.enums.EntityType;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File extends BaseTimeEntity {

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

    public void setEntityData(FileRequestDto requestDto) {
        this.entityType = requestDto.getEntityType();
        this.typeId = requestDto.getEntityId();
    }

    public void setEntityDataForUser(FileUserDto userDto) {
        this.entityType = userDto.getEntityType();
        this.email = userDto.getEmail();
    }

    public void update(FileUpdateDto updateDto) {
        this.entityType = updateDto.getEntityType();
        this.typeId = updateDto.getEntityId();
        this.email = updateDto.getEmail();
    }
}
