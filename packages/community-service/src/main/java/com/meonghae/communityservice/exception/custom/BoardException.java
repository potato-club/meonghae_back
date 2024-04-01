package com.meonghae.communityservice.exception.custom;

import com.meonghae.communityservice.exception.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;
}
