package com.meonghae.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestPart;

@FeignClient(name = "profile-service")
public interface PetServiceClient {

    @DeleteMapping("/users")
    ResponseEntity<String> deletedByUserEmail(@RequestPart String email);
}
