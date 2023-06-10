package com.meonghae.userservice.error.exception;


import com.meonghae.userservice.error.ErrorCode;

public class S3Exception extends BusinessException {
    public S3Exception(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
