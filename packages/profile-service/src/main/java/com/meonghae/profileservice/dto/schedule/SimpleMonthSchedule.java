package com.meonghae.profileservice.dto.schedule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SimpleMonthSchedule {
    private int month;
    private List<SimpleSchedule> schedules;
}
