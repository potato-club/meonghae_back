package com.moenghae.apigatewayservice.error;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class ErrorEntity {

    private int code;
    private String status;
    private String message;

    @Builder
    public ErrorEntity(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
