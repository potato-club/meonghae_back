package com.meonghae.communityservice.Exception;

import com.meonghae.communityservice.Exception.Custom.BoardException;
import com.meonghae.communityservice.Exception.Custom.CommentException;
import com.meonghae.communityservice.Exception.Error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBoardException(BoardException ex) {
        log.error("Board Exception", ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorMessage());
        return new ResponseEntity<>(response, ex.getErrorCode().getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleCommentException(CommentException ex) {
        log.error("Comment Exception", ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorMessage());
        return new ResponseEntity<>(response, ex.getErrorCode().getStatus());
    }
}
