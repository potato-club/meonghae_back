package com.meonghae.userservice.infra.entity;

import com.meonghae.userservice.domin.FCMToken.FCMToken;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "fcmtoken")
public class FCMTokenEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(unique = true, nullable = false)
    private String email;

    public static FCMTokenEntity from(FCMToken fcmToken) {
        FCMTokenEntity fcmTokenEntity = new FCMTokenEntity();
        fcmTokenEntity.id = fcmToken.getId();
        fcmTokenEntity.token = fcmToken.getToken();
        fcmTokenEntity.email = fcmToken.getEmail();

        return fcmTokenEntity;
    }

    public FCMToken toModel() {
        return FCMToken.builder()
                .id(id)
                .email(email)
                .token(token)
                .build();
    }
}
