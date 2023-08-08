package com.meonghae.profileservice.repository;

import com.meonghae.profileservice.entity.RecurringSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringScheduleRepository extends JpaRepository<RecurringSchedule,Long> {
}
