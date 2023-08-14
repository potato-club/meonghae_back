package com.meonghae.profileservice.repository;

import com.meonghae.profileservice.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByUserEmail(String userEmail);
    void deleteAllByUserEmail(String userEmail);
}
