package com.meonghae.profileservice.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
// @AllArgsConstructor
public enum ErrorCode {
  RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, 001, "잘못된 요청방식입니다."),
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST,002,"없는 유저입니다."),
  CANT_READ_TOKEN(HttpStatus.UNAUTHORIZED,003,"토큰 값을 읽을 수 없습니다."),

  //    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED,"E0001","로그인하세요."),
  //    WRONG_ID_PW_EXCEPTION(HttpStatus.UNAUTHORIZED,"E0011","잘못된 아이디 또는 비밀번호입니다."),
  //    UNDEFINED_TIME(HttpStatus.FORBIDDEN,"E00032","정의되어있는 시간이 없습니다."),
  //    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"E0004","잘못된 주소입니다."),
  //    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"E0005","예기치 못한 오류입니다."),
  NOT_FOUND_PET(HttpStatus.NOT_FOUND, 101, "반려동물을 찾을 수 없습니다.");
  private HttpStatus status;

  private int code;

  private String message;

  ErrorCode(HttpStatus status, int code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }
}
