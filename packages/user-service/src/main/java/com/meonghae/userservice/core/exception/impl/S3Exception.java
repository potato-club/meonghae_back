package com.meonghae.userservice.core.exception.impl;


import com.meonghae.userservice.core.exception.ErrorCode;

public class S3Exception extends BusinessException {
    public S3Exception(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
