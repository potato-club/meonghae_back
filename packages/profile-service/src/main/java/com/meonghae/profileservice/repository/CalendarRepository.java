package com.meonghae.profileservice.repository;

import com.meonghae.profileservice.dto.CalendarPK;
import com.meonghae.profileservice.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, CalendarPK> {
}
