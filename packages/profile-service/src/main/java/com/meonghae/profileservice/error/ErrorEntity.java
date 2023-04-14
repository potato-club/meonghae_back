package com.meonghae.profileservice.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorEntity {
    private int errorCode;

    private String errorMessage;

    @Builder
    public ErrorEntity(HttpStatus httpStatus, int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
