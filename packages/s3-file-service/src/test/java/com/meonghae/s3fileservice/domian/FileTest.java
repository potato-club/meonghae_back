package com.meonghae.s3fileservice.domian;

import com.meonghae.s3fileservice.domain.File;
import com.meonghae.s3fileservice.domain.enums.EntityType;
import com.meonghae.s3fileservice.dto.FileRequest;
import com.meonghae.s3fileservice.dto.FileUpdate;
import com.meonghae.s3fileservice.dto.FileUser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileTest {

    @Test
    void 유저_프로필_사진을_업로드할_수_있다() {
        // given
        File file = File.builder()
                .fileName("test-picture-123-456")
                .fileUrl("https://s3.ap-northeast-2.amazonaws.com/test/image/example1.png")
                .build();

        FileUser request = FileUser.builder()
                .entityType(EntityType.USER)
                .email("test@test.com")
                .build();

        // when
        file.uploadForUser(request);

        // then
        assertThat(file.getEntityType()).isEqualTo(request.getEntityType());
        assertThat(file.getEmail()).isEqualTo(request.getEmail());
        assertThat(file.getTypeId()).isNull();
    }

    @Test
    void 유저_프로필_사진_외_다른_사진을_업로드할_수_있다() {
        // given
        File file = File.builder()
                .fileName("test-picture-pet-123-456")
                .fileUrl("https://s3.ap-northeast-2.amazonaws.com/test/image/example2.png")
                .build();

        FileRequest request = FileRequest.builder()
                .entityType(EntityType.PET)
                .entityId(1L)
                .build();

        // when
        file.uploadForData(request);

        // then
        assertThat(file.getEntityType()).isEqualTo(request.getEntityType());
        assertThat(file.getTypeId()).isEqualTo(request.getEntityId());
        assertThat(file.getEmail()).isNull();
    }

    @Test
    void 유저_프로필_사진을_업데이트할_수_있다() {
        // given
        File file = File.builder()
                .fileName("test-picture-user-987-654")
                .fileUrl("https://s3.ap-northeast-2.amazonaws.com/test/image/example3.png")
                .build();

        FileUpdate request = FileUpdate.builder()
                .entityType(EntityType.USER)
                .email("test@test.com")
                .build();

        // when
        file.update(request);

        // then
        assertThat(file.getEntityType()).isEqualTo(request.getEntityType());
        assertThat(file.getEmail()).isEqualTo(request.getEmail());
        assertThat(file.getTypeId()).isNull();
    }

    @Test
    void 유저_프로필_사진_외_다른_사진을_업데이트할_수_있다() {
        // given
        List<File> files = new ArrayList<>();
        List<FileUpdate> requests = new ArrayList<>();

        int count = 0;

        for (int i = 0; i < 3; i++) {
            File file = File.builder()
                    .fileName("test-picture-pet-123-456/" + i)
                    .fileUrl("https://s3.ap-northeast-2.amazonaws.com/test/image/" + i + "/example.png")
                    .build();

            FileUpdate update = FileUpdate.builder()
                    .entityType(EntityType.BOARD)
                    .entityId(1L)
                    .build();

            files.add(file);
            requests.add(update);
        }

        // when
        for (File file : files) {
            file.update(requests.get(count++));
        }

        count = 0;

        // then
        for (File file : files) {
            assertThat(file.getEntityType()).isEqualTo(requests.get(count).getEntityType());
            assertThat(file.getTypeId()).isEqualTo(requests.get(count++).getEntityId());
            assertThat(file.getEmail()).isNull();
        }
    }
}
