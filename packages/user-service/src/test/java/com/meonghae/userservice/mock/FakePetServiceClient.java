package com.meonghae.userservice.mock;

import com.meonghae.userservice.service.client.feign.PetServiceClient;
import org.springframework.http.ResponseEntity;

public class FakePetServiceClient implements PetServiceClient {

    @Override
    public ResponseEntity<String> deletedByUserEmail(String email) {
        return ResponseEntity.ok("토큰 삭제 완료");
    }

    @Override
    public void getReviseFcmToken(String email, String fcmToken) {

    }
}
