package com.meonghae.profileservice.dto.calendar;

import lombok.Getter;

@Getter
public class CalendarRequestDTO {
  private Long petId;
  private int year;
  private int month;
  private int day;
  private int hour;
  private int minute;
  private String text;
}
