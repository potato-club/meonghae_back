package com.meonghae.profileservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
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

public class CalendarData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Calendar와 다대일 관계 설정, 외래키로 사용할 컬럼 지정
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name ="year" , referencedColumnName = "year"),
            @JoinColumn(name ="month" , referencedColumnName = "month"),
            @JoinColumn(name ="day" , referencedColumnName = "day")
    })
    @JsonBackReference
    private Calendar calendar;
    private String email;
    private String text;
}
