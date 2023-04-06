package com.meonghae.profileservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Calendar")
@IdClass(CalendarEntity.class)
public class CalendarEntity {

    @Id
    private int year;

    @Id
    private int month;

    @Id
    private int day;


    @MapsId
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "year",referencedColumnName = "year"),
            @JoinColumn(name = "month",referencedColumnName = "month"),
            @JoinColumn(name = "day",referencedColumnName = "day")
    })
    List<CalendarData> calendarDataList;

}
