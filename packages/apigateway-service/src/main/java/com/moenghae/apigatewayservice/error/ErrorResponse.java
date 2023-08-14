package com.moenghae.apigatewayservice.error;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorResponse {

    private ErrorCode errorCode;
    private String errorMessage;
}
