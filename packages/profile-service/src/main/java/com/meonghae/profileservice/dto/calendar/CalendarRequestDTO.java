package com.meonghae.profileservice.dto.calendar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meonghae.profileservice.enumCustom.VaccinationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CalendarRequestDTO {
  @ApiModelProperty(notes = "반려동물 id", example = "LONG", required = true)
  private Long petId;
  @ApiModelProperty(notes = "백신 지정할 타입", example = "DHPPL, Coronavirus 등 string값")
  private VaccinationType vaccinationType;
  @ApiModelProperty(notes = "일정 날짜와 시간", example = "2023-01-01", required = true)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate scheduleTime;
  @ApiModelProperty(notes = "알림 지정 시간 ", example = "2023-01-01THH:mm")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime alarmTime;
  @ApiModelProperty(notes = "일정 정보", example = "예방주사 맞는 날", required = true)
  private String text;
}
