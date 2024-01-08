package com.meonghae.profileservice.dto.schedule;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SimpleSchedule {
    private int day;
    private List<Long> scheduleIds;

    public SimpleSchedule (int day,long scheduleIds) {
        this.day = day;
        this.scheduleIds = new ArrayList<>();
        this.scheduleIds.add(scheduleIds);
    }
}
