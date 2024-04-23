package com.meonghae.userservice.core.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorEntity {

    private int status;
    private String errorCode;
    private String errorMessage;

    @Builder
    public ErrorEntity(int status, String errorCode, String errorMessage) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
