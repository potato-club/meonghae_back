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

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ErrorExceptionControllerAdvice {

    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(HttpServletRequest request, final InvalidTokenException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(HttpServletRequest request, final IllegalArgumentException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({JwtExpiredException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(HttpServletRequest request, final JwtExpiredException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({SignatureException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(HttpServletRequest request, final SignatureException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({UnsupportedJwtException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(HttpServletRequest request, final UnsupportedJwtException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }
}
