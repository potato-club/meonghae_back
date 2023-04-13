package com.meonghae.profileservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.meonghae.profileservice.dto.CalendarPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(CalendarPK.class)

public class Calendar {
    @Id
    private int year;
    @Id
    private int month;
    @Id
    private int day;

    @OneToMany(mappedBy = "calendar",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonManagedReference
    List<CalendarData> calendarDataList;

}
