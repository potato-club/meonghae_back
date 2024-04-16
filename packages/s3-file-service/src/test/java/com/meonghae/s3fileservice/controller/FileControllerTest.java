package com.meonghae.s3fileservice.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import com.meonghae.s3fileservice.dto.FileUser;
import com.meonghae.s3fileservice.mock.FakeS3URL;
import com.meonghae.s3fileservice.service.port.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
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
    private FileRepository fileRepository;

    @Autowired
    private AmazonS3Client amazonS3;

    @BeforeEach
    void init() throws MalformedURLException {
        FakeS3URL s3URL = new FakeS3URL();
        given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(new PutObjectResult());
        given(amazonS3.getUrl(any(), any())).willReturn(s3URL.getAmazonS3Url("test-s3", any()));
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

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/files/users")
                                .file(mock)
                                .param("email", request.getEmail())
                                .param("entityType", request.getEntityType().toString())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}
