package com.meonghae.profileservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "CalendarEntity")
@IdClass(CalendarDataEntityPK.class)
@SequenceGenerator(
        name = "CALENDAR_DATA_GENERATOR",
        sequenceName = "CALENDAR_DATA_SEQUENCE",
        initialValue = 1,
        allocationSize = 1)

public class CalendarDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Calendar_Data_Sequence")
    private long seq;

    private int year;

    private int month;

    private int day;

    private String name;

    private long userId;

    private String title;

    @ColumnDefault("0")
    private int bet;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
