package com.meonghae.userservice.entity;

import com.meonghae.userservice.dto.UserRequestDto;
import com.meonghae.userservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "users")
public class User {

    @Id
    private String uid;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;  // 유저의 직업을 나타내는 Enum (ex. USER, ARTIST)

    @Column
    private boolean deleted;

    public void update(UserRequestDto userDto) {
        this.nickname = userDto.getNickname();
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
