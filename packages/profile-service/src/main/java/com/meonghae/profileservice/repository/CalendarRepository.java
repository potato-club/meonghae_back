package com.meonghae.profileservice.repository;

import com.meonghae.profileservice.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}
