package com.moenghae.apigatewayservice.error;

import com.moenghae.apigatewayservice.error.jwt.InvalidTokenException;
import com.moenghae.apigatewayservice.error.jwt.JwtExpiredException;
import com.moenghae.apigatewayservice.error.jwt.SignatureException;
import com.moenghae.apigatewayservice.error.jwt.UnsupportedJwtException;
import com.moenghae.apigatewayservice.error.jwt.IllegalArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class ErrorExceptionControllerAdvice {

    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(InvalidTokenException e) {
        return ResponseEntity
                .status(e.getErrorCode().getCode())
                .body(ErrorEntity.builder()
                        .code(e.getErrorCode().getCode())
                        .status(e.getErrorCode().getStatus())
                        .message(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(IllegalArgumentException e) {
        return ResponseEntity
                .status(e.getErrorCode().getCode())
                .body(ErrorEntity.builder()
                        .code(e.getErrorCode().getCode())
                        .status(e.getErrorCode().getStatus())
                        .message(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({JwtExpiredException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(JwtExpiredException e) {
        return ResponseEntity
                .status(e.getErrorCode().getCode())
                .body(ErrorEntity.builder()
                        .code(e.getErrorCode().getCode())
                        .status(e.getErrorCode().getStatus())
                        .message(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({SignatureException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(SignatureException e) {
        return ResponseEntity
                .status(e.getErrorCode().getCode())
                .body(ErrorEntity.builder()
                        .code(e.getErrorCode().getCode())
                        .status(e.getErrorCode().getStatus())
                        .message(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({UnsupportedJwtException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(UnsupportedJwtException e) {
        return ResponseEntity
                .status(e.getErrorCode().getCode())
                .body(ErrorEntity.builder()
                        .code(e.getErrorCode().getCode())
                        .status(e.getErrorCode().getStatus())
                        .message(e.getErrorCode().getMessage())
                        .build());
    }
}
