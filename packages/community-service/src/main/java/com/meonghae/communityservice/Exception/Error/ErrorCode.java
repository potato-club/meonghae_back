package com.meonghae.communityservice.Exception.Error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@ToString
public enum ErrorCode {
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    NOT_FOUND(404, HttpStatus.NOT_FOUND, "페이지를 찾을 수 없습니다."),
    CONFLICT(409, HttpStatus.CONFLICT, "서버 상태와 충돌합니다."),
    INTERNAL_SERVER_ERROR(500,HttpStatus.INTERNAL_SERVER_ERROR,"내부 서버 오류입니다.")
    ;

    private int code;
    private HttpStatus status;
    private String message;
}