package com.moenghae.apigatewayservice.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<String> handleCustomJwtException(CustomJwtException e) {
        int errorCode = e.getErrorCode();
        String errorMessage = e.getMessage();
        return ResponseEntity.status(errorCode).body(errorMessage);
    }
}
