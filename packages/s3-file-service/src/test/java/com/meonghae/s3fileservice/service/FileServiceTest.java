package com.meonghae.s3fileservice.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.meonghae.s3fileservice.domain.File;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import com.meonghae.s3fileservice.dto.FileRequest;
import com.meonghae.s3fileservice.dto.FileResponse;
import com.meonghae.s3fileservice.dto.FileUpdate;
import com.meonghae.s3fileservice.dto.FileUser;
import com.meonghae.s3fileservice.mock.FakeFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class FileServiceTest {

    private FileServiceImpl fileService;
    private FakeFileRepository fileRepository;

    @BeforeEach
    void init() throws MalformedURLException {
        AmazonS3Client client = Mockito.mock(AmazonS3Client.class);
        this.fileRepository = new FakeFileRepository();

        this.fileService = FileServiceImpl.builder()
                .s3Client(client)
                .fileRepository(fileRepository)
                .bucketName("test-s3")
                .build();

        when(client.getUrl(anyString(), anyString()))
                .thenReturn(new URL("https://s3.ap-northeast-2.amazonaws.com/test/image/test1.png"));
    }

    @Test
    void 유저_서비스로부터_전달받은_사진을_저장한다() throws IOException {
        // given
        MockMultipartFile mock = new MockMultipartFile("file",
                "test1.png",
                "image/png",
                "test-file1".getBytes(StandardCharsets.UTF_8));

        FileUser request = FileUser.builder()
                .entityType(EntityType.USER)
                .email("test@test.com")
                .build();

        // when
        fileService.uploadFileForUser(mock, request);
        File file = fileRepository.findByEntityTypeAndEmail(EntityType.USER, request.getEmail()).get(0);

        // then
        assertThat(file.getEmail()).isEqualTo(request.getEmail());
        assertThat(file.getEntityType()).isEqualTo(request.getEntityType());
        assertThat(file.getTypeId()).isNull();
        assertThat(file.getFileName()).isNotNull();
        assertThat(file.getFileUrl()).isNotNull();
    }

    @Test
    void 유저_서비스_외_다른_서비스로부터_전달받은_사진을_저장한다() throws IOException {
        // given
        List<MultipartFile> list = new ArrayList<>();

        MockMultipartFile mock1 = new MockMultipartFile("file",
                "test1.png",
                "image/png",
                "test-file1".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile mock2 = new MockMultipartFile("file",
                "test2.png",
                "image/png",
                "test-file2".getBytes(StandardCharsets.UTF_8));

        FileRequest request = FileRequest.builder()
                .entityType(EntityType.BOARD)
                .entityId(1L)
                .build();

        list.add(mock1);
        list.add(mock2);

        // when
        fileService.uploadImages(list, request);
        List<File> files = fileRepository.findByEntityTypeAndTypeId(request.getEntityType(), request.getEntityId());

        // then
        for (File file : files) {
            assertThat(file.getEmail()).isNull();
            assertThat(file.getEntityType()).isEqualTo(request.getEntityType());
            assertThat(file.getTypeId()).isEqualTo(request.getEntityId());
            assertThat(file.getFileName()).isNotNull();
            assertThat(file.getFileUrl()).isNotNull();
        }
    }

    @Test
    void 업데이트_요청을_받은_사진은_지운_뒤_새_사진으로_저장한다() throws IOException {
        // given
        List<MultipartFile> list = new ArrayList<>();
        List<FileUpdate> updateList = new ArrayList<>();

        MockMultipartFile mock1 = new MockMultipartFile("file1",
                "test1.png",
                "image/png",
                "test-file1".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile mock2 = new MockMultipartFile("file2",
                "test2.png",
                "image/png",
                "test-file2".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile mock3 = new MockMultipartFile("file3",
                "test3.png",
                "image/png",
                "test-file3".getBytes(StandardCharsets.UTF_8));

        list.add(mock1);
        list.add(mock2);
        list.add(mock3);

        FileRequest request = FileRequest.builder()
                .entityType(EntityType.BOARD)
                .entityId(1L)
                .build();

        fileService.uploadImages(list, request);

        List<FileResponse> fileList = fileRepository.getFileList(request);
        list = new ArrayList<>();

        for (int i = 0; i < fileList.size(); i++) {
            FileUpdate fileUpdate;
            if (i == 0) {
                fileUpdate = FileUpdate.builder()
                        .fileName(fileList.get(i).getFileName())
                        .fileUrl(fileList.get(i).getFileUrl())
                        .entityType(fileList.get(i).getEntityType())
                        .entityId(fileList.get(i).getTypeId())
                        .deleted(false)
                        .build();
            } else {
                fileUpdate = FileUpdate.builder()
                        .fileName(fileList.get(i).getFileName())
                        .fileUrl(fileList.get(i).getFileUrl())
                        .entityType(fileList.get(i).getEntityType())
                        .entityId(fileList.get(i).getTypeId())
                        .deleted(true)
                        .build();

                list.add(mock3);
            }

            updateList.add(fileUpdate);
        }

        // when
        fileService.updateFiles(list, updateList);
        List<File> files = fileRepository.findByEntityTypeAndTypeId(fileList.get(0).getEntityType(),
                fileList.get(0).getTypeId());

        // then
        for (int i = 0; i < files.size(); i++) {
            assertThat(files.get(i).getEmail()).isNull();
            assertThat(files.get(i).getEntityType()).isEqualTo(fileList.get(i).getEntityType());
            assertThat(files.get(i).getTypeId()).isEqualTo(fileList.get(i).getTypeId());

            if (i == 0) {
                assertThat(files.get(i).getFileName()).isEqualTo(fileList.get(i).getFileName());
                assertThat(files.get(i).getFileUrl()).isEqualTo(fileList.get(i).getFileUrl());
            } else {
                assertThat(files.get(i).getFileName()).isNotEqualTo(fileList.get(i).getFileName());
                assertThat(files.get(i).getFileUrl()).isNotEqualTo(fileList.get(i).getFileUrl());
            }
        }
    }

    @Test
    void 유저_서비스의_사진을_삭제한다() throws IOException {
        // given
        MockMultipartFile mock = new MockMultipartFile("file",
                "test1.png",
                "image/png",
                "test-file1".getBytes(StandardCharsets.UTF_8));

        FileUser request = FileUser.builder()
                .entityType(EntityType.USER)
                .email("test@test.com")
                .build();

        fileService.uploadFileForUser(mock, request);

        // when
        fileService.deleteFileForUser(request);
        List<File> files = fileRepository.findByEntityTypeAndEmail(EntityType.USER, request.getEmail());

        // then
        assertThat(files).isEmpty();
    }

    @Test
    void 해당_서비스의_Id_가_같은_사진들을_모두_삭제한다() throws IOException {
        // given
        List<MultipartFile> list = new ArrayList<>();

        MockMultipartFile mock1 = new MockMultipartFile("file",
                "test1.png",
                "image/png",
                "test-file1".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile mock2 = new MockMultipartFile("file",
                "test2.png",
                "image/png",
                "test-file2".getBytes(StandardCharsets.UTF_8));

        FileRequest request = FileRequest.builder()
                .entityType(EntityType.BOARD)
                .entityId(1L)
                .build();

        list.add(mock1);
        list.add(mock2);

        fileService.uploadImages(list, request);

        // when
        fileService.deleteFiles(request);
        List<File> files = fileRepository.findByEntityTypeAndTypeId(request.getEntityType(), request.getEntityId());

        // then
        assertThat(files).isEmpty();
    }
}
