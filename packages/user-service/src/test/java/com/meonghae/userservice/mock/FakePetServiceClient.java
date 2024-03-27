package com.meonghae.userservice.mock;

import com.meonghae.userservice.service.client.feign.PetServiceClient;
import org.springframework.http.ResponseEntity;

public class FakePetServiceClient implements PetServiceClient {

    @Override
    public ResponseEntity<String> deletedByUserEmail(String email) {
        return null;
    }

    @Override
    public void getReviseFcmToken(String email, String fcmToken) {

    }
}
