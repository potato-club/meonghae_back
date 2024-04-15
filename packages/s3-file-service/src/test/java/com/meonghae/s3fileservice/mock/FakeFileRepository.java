package com.meonghae.s3fileservice.mock;

import com.meonghae.s3fileservice.domain.File;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import com.meonghae.s3fileservice.dto.FileRequest;
import com.meonghae.s3fileservice.dto.FileResponse;
import com.meonghae.s3fileservice.dto.FileUserResponse;
import com.meonghae.s3fileservice.service.port.FileRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeFileRepository implements FileRepository {

    private final List<File> data = Collections.synchronizedList(new ArrayList<>());

    @Override
    public List<File> findByEntityTypeAndTypeId(EntityType entityType, Long typeId) {
        return data.stream()
                .filter(file -> file.getEntityType().equals(entityType))
                .filter(file -> file.getTypeId().equals(typeId))
                .collect(Collectors.toList());
    }

    @Override
    public List<File> findByEntityTypeAndEmail(EntityType entityType, String email) {
        return data.stream()
                .filter(file -> file.getEntityType().equals(entityType))
                .filter(file -> file.getEmail().equals(email))
                .collect(Collectors.toList());
    }

    @Override
    public void save(File file) {
        if (file.getEmail() == null) {
            data.removeIf(item -> Objects.equals(item.getTypeId(), file.getTypeId()));
        } else {
            data.removeIf(item -> Objects.equals(item.getEmail(), file.getEmail()));
        }

        data.add(file);
    }

    @Override
    public void delete(File file) {
        data.remove(file);
    }

    @Override
    public List<FileResponse> getFileList(FileRequest request) {
        List<FileResponse> list = new ArrayList<>();
        List<File> files = data.stream()
                .filter(file -> file.getEntityType().equals(request.getEntityType()))
                .filter(file -> file.getTypeId().equals(request.getEntityId()))
                .collect(Collectors.toList());

        for (File file : files) {
            FileResponse response = FileResponse.builder()
                    .fileName(file.getFileName())
                    .fileUrl(file.getFileUrl())
                    .entityType(file.getEntityType())
                    .typeId(file.getTypeId())
                    .build();

            list.add(response);
        }

        return list;
    }

    @Override
    public FileUserResponse getUserProfile(String email) {
        Optional<File> file = data.stream()
                .filter(item -> item.getEntityType().equals(EntityType.USER))
                .filter(item -> item.getEmail().equals(email))
                .findAny();

        if (file.isEmpty()) {
            return new FileUserResponse(null, null, null, null);
        }

        return FileUserResponse.builder()
                .fileName(file.get().getFileName())
                .fileUrl(file.get().getFileUrl())
                .entityType(file.get().getEntityType())
                .email(file.get().getEmail())
                .build();
    }

    @Override
    public FileUserResponse getPetProfile(FileRequest request) {
        Optional<File> file = data.stream()
                .filter(item -> item.getEntityType().equals(request.getEntityType()))
                .filter(item -> item.getTypeId().equals(request.getEntityId()))
                .findAny();

        if (file.isEmpty()) {
            return new FileUserResponse(null, null, null, null);
        }

        return FileUserResponse.builder()
                .fileName(file.get().getFileName())
                .fileUrl(file.get().getFileUrl())
                .entityType(file.get().getEntityType())
                .email(null)
                .build();
    }
}
