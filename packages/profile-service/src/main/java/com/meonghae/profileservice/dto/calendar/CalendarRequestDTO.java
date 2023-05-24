package com.meonghae.profileservice.dto.calendar;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CalendarRequestDTO {
  private Long petId;
  private LocalDateTime ScheduleTime;
  private String text;
}
