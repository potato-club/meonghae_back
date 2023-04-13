package com.meonghae.profileservice.repository;

import com.meonghae.profileservice.entity.CalendarData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarDataRepository extends JpaRepository<CalendarData,Long> {
}
