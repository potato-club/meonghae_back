package com.meonghae.communityservice.Exception.Custom;

import com.meonghae.communityservice.Exception.Error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String errorMessage;
}
