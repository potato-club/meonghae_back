package com.moenghae.apigatewayservice.error;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

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
