package com.meonghae.profileservice.dto.schedule;

import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleOfDayRequestDto {
    private int year;
    private int month;
    private Integer day;
    private List<Long> scheduleId;
}
