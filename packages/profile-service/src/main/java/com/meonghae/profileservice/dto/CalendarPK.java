package com.meonghae.profileservice.dto;

import java.io.Serializable;
import java.util.Objects;

public class CalendarPK implements Serializable {

    private int year;
    private int month;
    private int day;

    public CalendarPK() {
    }

    public CalendarPK(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    // equals() 와 hashCode() 메서드를 재정의해야 합니다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarPK that = (CalendarPK) o;
        return year == that.year &&
                month == that.month &&
                day == that.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day);
    }
}