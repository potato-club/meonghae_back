package com.meonghae.profileservice.dto.calendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CalendarRequestDTO {
  @ApiModelProperty(notes = "반려동물 id", example = "LONG", required = true)
  private Long petId;
  @ApiModelProperty(notes = "일정 날짜와 시간", example = "2023-01-01 HH:mm:ss.sss", required = true)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private LocalDateTime ScheduleTime;
  @ApiModelProperty(notes = "일정 정보", example = "예방주사 맞는 날", required = true)
  private String text;
}
