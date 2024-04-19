package com.meonghae.s3fileservice.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import com.meonghae.s3fileservice.dto.*;
import com.meonghae.s3fileservice.mock.FakeS3URL;
import com.meonghae.s3fileservice.service.FileService;
import com.meonghae.s3fileservice.service.port.FileRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private AmazonS3Client amazonS3;

    @BeforeEach
    void init() throws MalformedURLException {
        FakeS3URL s3URL = new FakeS3URL();
        given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(new PutObjectResult());
        given(amazonS3.getUrl(anyString(), anyString()))
                .willReturn(s3URL.getAmazonS3Url("test-s3", UUID.randomUUID() + "test1.png"));
    }

    @BeforeAll
    public static void setUp(@Autowired DataSource dataSource) {
        // 스프링 컨텍스트가 로드된 후, 한 번만 실행되는 초기화 블록
        ResourceDatabasePopulator pt = new ResourceDatabasePopulator();
        pt.addScript(new ClassPathResource("/sql/controller-test-data.sql"));
        pt.execute(dataSource);
    }

    @AfterEach
    void cleanUp(@Autowired DataSource dataSource) {
        // 모든 테스트가 완료된 후, 데이터 정리
        ResourceDatabasePopulator pt = new ResourceDatabasePopulator();
        pt.addScript(new ClassPathResource("/sql/delete-all-data.sql"));
        pt.execute(dataSource);
    }

    @Test
    void 유저_서비스의_사진을_업로드한다() throws Exception {
        // given
        MockMultipartFile mock = new MockMultipartFile("file",
                "test1.png",
                "image/png",
                "test-file1".getBytes(StandardCharsets.UTF_8));

        FileUser request = FileUser.builder()
                .entityType(EntityType.USER)
                .email("test@test.com")
                .build();

        String content = objectMapper.writeValueAsString(request);
        MockMultipartFile json = new MockMultipartFile("data",
                "jsonData",
                MediaType.APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8));

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/files/users")
                                .file(mock)
                                .file(json)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        FileUserResponse file = fileRepository.getUserProfile(request.getEmail());

        assertThat(file.getFileName()).isNotNull();
        assertThat(file.getFileUrl()).isNotNull();
        assertThat(file.getEmail()).isEqualTo(request.getEmail());
        assertThat(file.getEntityType()).isEqualTo(request.getEntityType());
    }

    @Test
    void 유저_서비스_외_다른_서비스의_사진을_업로드한다() throws Exception {
        // given
        MockMultipartFile mock = new MockMultipartFile("files",
                "test1.png",
                "image/png",
                "test-file1".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile mock2 = new MockMultipartFile("files",
                "test2.png",
                "image/png",
                "test-file2".getBytes(StandardCharsets.UTF_8));

        FileRequest request = FileRequest.builder()
                .entityType(EntityType.BOARD)
                .entityId(1L)
                .build();

        String content = objectMapper.writeValueAsString(request);
        MockMultipartFile json = new MockMultipartFile("data",
                "jsonData",
                MediaType.APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8));

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/files")
                                .file(mock)
                                .file(mock2)
                                .file(json)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        List<FileResponse> files = fileRepository.getFileList(request);

        for (FileResponse file : files) {
            assertThat(file.getFileName()).isNotNull();
            assertThat(file.getFileUrl()).isNotNull();
            assertThat(file.getEntityType()).isEqualTo(request.getEntityType());
            assertThat(file.getTypeId()).isEqualTo(request.getEntityId());
        }
    }

    @Test
    void 유저_서비스의_사진을_업데이트한다() throws Exception {
        // given
        List<FileUpdate> list = new ArrayList<>();

        MockMultipartFile mock = new MockMultipartFile("files",
                "test1-1.png",
                "image/png",
                "test-file1-1".getBytes(StandardCharsets.UTF_8));

        this.setMockFile(EntityType.USER);
        FileUserResponse file = fileRepository.getUserProfile("test@test.com");

        FileUpdate request = FileUpdate.builder()
                .fileName(file.getFileName())
                .fileUrl(file.getFileUrl())
                .entityType(EntityType.USER)
                .email("test@test.com")
                .deleted(true)
                .build();

        list.add(request);

        String content = objectMapper.writeValueAsString(list);
        MockMultipartFile json = new MockMultipartFile("dataList",
                "jsonData",
                MediaType.APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8));

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/files")
                                .file(mock)
                                .file(json)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        FileUserResponse response = fileRepository.getUserProfile(request.getEmail());

        assertThat(response.getFileName()).isNotEqualTo(file.getFileName());
        assertThat(response.getFileUrl()).isNotEqualTo(file.getFileUrl());
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getEntityType()).isEqualTo(request.getEntityType());
    }

    @Test
    void 유저_서비스_외_다른_서비스의_사진을_업데이트한다() throws Exception {
        // given
        List<FileUpdate> list = new ArrayList<>();

        MockMultipartFile mock = new MockMultipartFile("files",
                "test1-1.png",
                "image/png",
                "test-file1-1".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile mock2 = new MockMultipartFile("files",
                "test2-1.png",
                "image/png",
                "test-file2-1".getBytes(StandardCharsets.UTF_8));

        this.setMockFile(EntityType.BOARD);
        List<FileResponse> files = fileRepository.getFileList(new FileRequest(EntityType.BOARD, 1L));

        for (FileResponse fileResponse : files) {
            FileUpdate request = FileUpdate.builder()
                    .fileName(fileResponse.getFileName())
                    .fileUrl(fileResponse.getFileUrl())
                    .entityType(fileResponse.getEntityType())
                    .entityId(fileResponse.getTypeId())
                    .deleted(true)
                    .build();

            list.add(request);
        }

        String content = objectMapper.writeValueAsString(list);
        MockMultipartFile json = new MockMultipartFile("dataList",
                "jsonData",
                MediaType.APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8));

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/files")
                                .file(mock)
                                .file(mock2)
                                .file(json)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        List<FileResponse> responseList = fileRepository.getFileList(new FileRequest(EntityType.BOARD, 1L));

        for (int i = 0; i < responseList.size(); i++) {
            assertThat(responseList.get(i).getFileName()).isNotEqualTo(list.get(i).getFileName());
            assertThat(responseList.get(i).getFileUrl()).isNotEqualTo(list.get(i).getFileUrl());
            assertThat(responseList.get(i).getEntityType()).isEqualTo(list.get(i).getEntityType());
            assertThat(responseList.get(i).getTypeId()).isEqualTo(list.get(i).getEntityId());
        }
    }

    private void setMockFile(EntityType type) throws IOException {

        MockMultipartFile mock = new MockMultipartFile("files",
                "test1.png",
                "image/png",
                "test-file1".getBytes(StandardCharsets.UTF_8));

        MockMultipartFile mock2 = new MockMultipartFile("files",
                "test2.png",
                "image/png",
                "test-file2".getBytes(StandardCharsets.UTF_8));

        if (type.equals(EntityType.USER)) {
            fileService.uploadFileForUser(mock, new FileUser(type, "test@test.com"));
        } else {
            fileService.uploadImages(List.of(mock, mock2), new FileRequest(type, 1L));
        }

        FakeS3URL s3URL = new FakeS3URL();

        given(amazonS3.getUrl(anyString(), anyString()))
                .willReturn(s3URL.getAmazonS3Url("test-s3", UUID.randomUUID() + "test1-1.png"));
    }
}
