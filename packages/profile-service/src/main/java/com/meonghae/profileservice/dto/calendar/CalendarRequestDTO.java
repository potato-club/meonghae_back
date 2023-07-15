package com.meonghae.profileservice.dto.calendar;

import com.meonghae.profileservice.enumCustom.ScheduleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CalendarRequestDTO {
  @ApiModelProperty(notes = "반려동물 id", example = "LONG", required = true)
  private Long petId;
  @ApiModelProperty(notes = "사용자 지정 일정", example = "지정된 타입이 있으면 null처리")
  private String title;
  @ApiModelProperty(notes = "백신 지정할 타입", example = "DHPPL, Coronavirus 등 string값")
  private ScheduleType scheduleType;
  @ApiModelProperty(notes = "일정 날짜와 시간", example = "2023-01-01THH:mm", required = true)
  private LocalDateTime scheduleTime;
  @ApiModelProperty(notes = "알림 지정 시간 ", example = "2023-01-01THH:mm")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime alarmTime;
  @ApiModelProperty(notes = "일정 정보", example = "예방주사 맞는 날", required = true)
  private String text;

}
