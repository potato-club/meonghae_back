package com.meonghae.profileservice.dto.schedule;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SimpleSchedule {
    private int day;
    private List<Integer> scheduleIds;

    public SimpleSchedule (int day) {
        this.day = day;
        this.scheduleIds = new ArrayList<>();
    }
}
