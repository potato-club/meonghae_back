package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.calendar.CalendarRequestDTO;
import com.meonghae.profileservice.dto.calendar.CalendarResponseDTO;
import com.meonghae.profileservice.entity.Calendar;

import com.meonghae.profileservice.entity.PetEntity;
import com.meonghae.profileservice.entity.QCalendar;
import com.meonghae.profileservice.entity.QPetEntity;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.CalendarRepository;
import com.meonghae.profileservice.repository.PetRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {
    private final PetRepository petRepository;

    private final CalendarRepository calendarRepository;
    //dsl
    private final JPAQueryFactory jpaQueryFactory;

    //프로필 화면에서 가까운 일정 순서대로 표시하기위함.
    @Transactional
    public List<CalendarResponseDTO> getProfileSchedule(HttpServletRequest request) {
        LocalDate.now();
        QCalendar qCalendar = QCalendar.calendar;
        QPetEntity qPetEntity = QPetEntity.petEntity;
        List<Calendar> result = jpaQueryFactory.select(qCalendar)
                .from(qCalendar)
                .innerJoin(qCalendar.petEntity, qPetEntity)
                .where(qCalendar.user.eq("명재")
                        .and(qCalendar.scheduleTime.after(LocalDate.now().atStartOfDay())))
                .limit(30)
                .orderBy(qCalendar.scheduleTime.asc())
                .fetch();


        return result.stream().map(CalendarResponseDTO::new).collect(Collectors.toList());
    }

    //달력에서 출력하기 위함 - 날짜 하나 클릭시 그 날짜에 대한 일정들 리턴
    @Transactional
    public List<CalendarResponseDTO> getSchedule(CalendarRequestDTO calendarRequestDTO, HttpServletRequest request) {

        LocalDateTime startOfDay = LocalDate.of(calendarRequestDTO.getYear(), calendarRequestDTO.getMonth(), calendarRequestDTO.getDay()).atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1); //ex) 13일 23시 59분

        QCalendar qCalendar = QCalendar.calendar;
        QPetEntity qPetEntity = QPetEntity.petEntity;

        List<Calendar> result = jpaQueryFactory.select(qCalendar)
                .from(qCalendar)
                .innerJoin(qCalendar.petEntity,qPetEntity)
                .where(qCalendar.user.eq("명재")
                        .and(qCalendar.scheduleTime.between(startOfDay, endOfDay)))
                .orderBy(qCalendar.scheduleTime.asc())
                .fetch();

        return result.stream().map(CalendarResponseDTO::new).collect(Collectors.toList());
    }

    //달력 월단위 일정들 보기 위한 함수 - 같은 해 같은 월 데이터 출력
    @Transactional
    public List<CalendarResponseDTO> getMonthSechedule(CalendarRequestDTO calendarRequestDTO,HttpServletRequest request){
        LocalDate startOfDate = LocalDate.of(calendarRequestDTO.getYear(), calendarRequestDTO.getMonth(),1);
        LocalDate endOfDate = startOfDate.plusMonths(1).minusDays(1);

        QCalendar qCalendar = QCalendar.calendar;
        QPetEntity qPetEntity = QPetEntity.petEntity;

        List<Calendar> result = jpaQueryFactory.select(qCalendar)
                .from(qCalendar)
                .innerJoin(qCalendar.petEntity, qPetEntity)
                .where(qCalendar.user.eq("명재")
                        .and(qCalendar.scheduleTime.between(startOfDate.atStartOfDay(),endOfDate.atStartOfDay())))
                .orderBy(qCalendar.scheduleTime.asc())
                .fetch();
        return result.stream().map(CalendarResponseDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public String addSchedule(CalendarRequestDTO calendarRequestDTO, HttpServletRequest request) {
        //프론트에서 FM 이면 hour에 +12해서 주겠징
        LocalTime scheduleTime = LocalTime.of(calendarRequestDTO.getHour(), calendarRequestDTO.getMinute());
        LocalDate scheduleDate = LocalDate.of(calendarRequestDTO.getYear(), calendarRequestDTO.getMonth(), calendarRequestDTO.getDay());
        LocalDateTime scheduleDateTime = LocalDateTime.of(scheduleDate, scheduleTime);

        // 반려동물 지정
        PetEntity petEntity = petRepository.findById(calendarRequestDTO.getPetId()).orElseThrow(() -> {throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});

        Calendar calendar = new Calendar().builder()
                .user("명재")
                .petEntity(petEntity)
                .scheduleTime(scheduleDateTime)
                .text(calendarRequestDTO.getText())
                .createTime(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
        calendarRepository.save(calendar);
        return "일정 추가 완료";
    }

    // Update
    @Transactional
    public String updateSchedule(Long id, CalendarRequestDTO calendarRequestDTO, HttpServletRequest request){
        QCalendar qCalendar = QCalendar.calendar;
        QPetEntity qPetEntity = QPetEntity.petEntity;

        PetEntity petEntity = petRepository.findById(calendarRequestDTO.getPetId()).orElseThrow(() -> {throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});

        Calendar calendar = jpaQueryFactory.select(qCalendar)
                .from(qCalendar)
                .innerJoin(qCalendar.petEntity, qPetEntity)
                .where(qCalendar.id.eq(id))
                .fetchOne(); // 단일 건수의 데이터 조회 둘 이상일 경우 NonUniqueResultException

        calendar.update(calendarRequestDTO,petEntity);
        calendarRepository.save(calendar);
        return "수정 완료";
    }

    //Delete
    @Transactional
    public String deleteSchedule(Long id,HttpServletRequest request){

        calendarRepository.deleteById(id);
        return "삭제 완료";
    }
}