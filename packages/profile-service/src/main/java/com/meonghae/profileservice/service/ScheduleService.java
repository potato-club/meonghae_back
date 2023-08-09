package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.calendar.ScheduleRequestDTO;
import com.meonghae.profileservice.dto.calendar.ScheduleResponseDTO;
import com.meonghae.profileservice.entity.*;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.RecurringScheduleRepository;
import com.meonghae.profileservice.repository.ScheduleRepository;
import com.meonghae.profileservice.repository.PetRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ScheduleService {
  private final PetRepository petRepository;

  private final ScheduleRepository scheduleRepository;
  private final RecurringScheduleRepository recurringScheduleRepository;
  // dsl
  private final JPAQueryFactory jpaQueryFactory;
  private final FeignService feignService;


  // 프로필 화면에서 가까운 일정 순서대로 표시하기위함.
  @Transactional
  public List<ScheduleResponseDTO> getProfileSchedule(String token) {
    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QRecurringSchedule qRecurringSchedule = QRecurringSchedule.recurringSchedule;
    QPet qPet = QPet.pet;
    //일단 반복 없는 알림들 30개 뽑아놓고?
    List<Schedule> normalSchedules  =
            jpaQueryFactory
                    .select(qSchedule)
                    .from(qSchedule)
                    .leftJoin(qSchedule.pet, qPet)
                    .where(
                            qSchedule
                                    .userEmail
                                    .eq(userEmail)
                                    .and(qSchedule.scheduleTime.after(LocalDate.now().atStartOfDay())))
                    .limit(30)
                    .orderBy(qSchedule.scheduleTime.asc())
                    .fetch();

    List<ScheduleResponseDTO> resultList = normalSchedules.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());
    //반복 되는 알림들 뽑기
    List<RecurringSchedule> recurringScheduleList =
            jpaQueryFactory
                    .selectFrom(qRecurringSchedule)
                    .leftJoin(qRecurringSchedule.pet, qPet)
                    .where(qRecurringSchedule.userEmail.eq(userEmail))
                    .fetch();
    // 반복 일정을 가장 가까운 미래 일정으로 변환하여 allSchedules에 추가
    for ( RecurringSchedule recurringSchedule : recurringScheduleList ){
      LocalDateTime nextScheduleTime = recurringSchedule.getScheduleTime();
      while (nextScheduleTime.isBefore(LocalDateTime.now())) {
        nextScheduleTime = nextScheduleTime.plus(recurringSchedule.getScheduleType().getRepeatCycle(), ChronoUnit.MONTHS);
      }
      // nextScheduleTime은 일정이 예정된 가장 가까운 시간
      resultList.add(new ScheduleResponseDTO(recurringSchedule, nextScheduleTime));
    }
    // 일정을 시간순으로 정렬하고 상위 30개를 선택
    resultList.sort(Comparator.comparing(ScheduleResponseDTO::getScheduleTime));
    return resultList.stream().limit(30).collect(Collectors.toList());
  }

  //날짜 하나 클릭시 그 날짜에 대한 일정들 리턴
  @Transactional
  public List<ScheduleResponseDTO> getSchedule(
          LocalDateTime startOfDate, String token) {
    LocalDateTime endOfDate = startOfDate.plusDays(1).minusNanos(1); // ex)23시 59분

    String userEmail = feignService.getUserEmail(token);

    QRecurringSchedule qRecurringSchedule = QRecurringSchedule.recurringSchedule;
    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    List<Schedule> nomalScheduleList =
            jpaQueryFactory
                    .select(qSchedule)
                    .from(qSchedule)
                    .innerJoin(qSchedule.pet, qPet)
                    .where(
                            qSchedule.userEmail.eq(userEmail).and(qSchedule.scheduleTime.between(startOfDate, endOfDate)))
                    .orderBy(qSchedule.scheduleTime.asc())
                    .fetch();
    List<ScheduleResponseDTO> resultList =  nomalScheduleList.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());

    //유저가 저장한 반복되는 일정들을 모두 가져온다.
    List<RecurringSchedule> recurringScheduleList =
            jpaQueryFactory
                    .select(qRecurringSchedule)
                    .from(qRecurringSchedule)
                    .leftJoin(qRecurringSchedule.pet,qPet)
                    .where(
                            qRecurringSchedule.userEmail.eq(userEmail)
                    )
                    .fetch();

    for ( RecurringSchedule recurringSchedule : recurringScheduleList ) {
      //(기준 달 - 일정 달) % 반복주기가 0 이면서 기준 날과 day가 같은 일정 출력
      if ((startOfDate.getMonthValue() - recurringSchedule.getScheduleTime().getMonthValue())
              % recurringSchedule.getScheduleType().getRepeatCycle() == 0
              && startOfDate.getDayOfMonth() == recurringSchedule.getScheduleTime().getDayOfMonth()) {

        LocalDateTime intendedTime = LocalDateTime.of(
                startOfDate.getYear()
                ,startOfDate.getMonthValue()
                ,recurringSchedule.getScheduleTime().getDayOfMonth()
                ,recurringSchedule.getScheduleTime().getHour()
                ,recurringSchedule.getScheduleTime().getMinute(),0);

        resultList.add(new ScheduleResponseDTO(recurringSchedule,intendedTime));
      }
    }
    resultList.sort(Comparator.comparing(ScheduleResponseDTO::getScheduleTime));

    return resultList;

  }

  // 달력 월단위 일정들 보기 위한 함수 - 같은 해 같은 월 데이터 출력
  @Transactional
  public List<ScheduleResponseDTO> getMonthSchedule(LocalDate startOfDate, String token) {

    LocalDate endOfDate = startOfDate.plusMonths(1).minusDays(1);

    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QRecurringSchedule qRecurringSchedule = QRecurringSchedule.recurringSchedule;
    QPet qPet = QPet.pet;

    List<Schedule> nomalScheduleList =
            jpaQueryFactory
                    .select(qSchedule)
                    .from(qSchedule)
                    .innerJoin(qSchedule.pet, qPet)
                    .where(
                            qSchedule
                                    .userEmail
                                    .eq(userEmail)
                                    .and(
                                            qSchedule.scheduleTime.between(
                                                    startOfDate.atStartOfDay(), endOfDate.atStartOfDay())))
                    .orderBy(qSchedule.scheduleTime.asc())
                    .fetch();

    List<ScheduleResponseDTO> resultList = nomalScheduleList.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());

    //유저가 저장한 반복되는 일정들을 모두 가져온다.
    List<RecurringSchedule> recurringScheduleList =
            jpaQueryFactory
                    .select(qRecurringSchedule)
                    .from(qRecurringSchedule)
                    .leftJoin(qRecurringSchedule.pet,qPet)
                    .where(
                            qRecurringSchedule.userEmail.eq(userEmail)
                    )
                    .fetch();
    for ( RecurringSchedule recurringSchedule : recurringScheduleList ) {
      //(기준 달 - 일정 달) % 반복주기가 0인 일정들 출력 // 밑의 조건을 where 절에 추가 할 수 있지 않을까?
      if ((startOfDate.getMonthValue() - recurringSchedule.getScheduleTime().getMonthValue())
              % recurringSchedule.getScheduleType().getRepeatCycle() == 0 ) {
        LocalDateTime intendedTime = LocalDateTime.of(
                startOfDate.getYear(), startOfDate.getMonthValue(),recurringSchedule.getScheduleTime().getDayOfMonth(),recurringSchedule.getScheduleTime().getHour(),recurringSchedule.getScheduleTime().getMinute(),0);

        resultList.add(new ScheduleResponseDTO(recurringSchedule,intendedTime));
      }
    }
    resultList.sort(Comparator.comparing(ScheduleResponseDTO::getScheduleTime));

    return resultList;
  }


  @Transactional
  public List<ScheduleResponseDTO> getScheduleOfFindByText(String key, String token){
    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QRecurringSchedule qRecurringSchedule = QRecurringSchedule.recurringSchedule;
    QPet qPet = QPet.pet;

    List<Schedule> scheduleList = jpaQueryFactory
            .selectFrom(qSchedule)
            .innerJoin(qSchedule.pet, qPet).fetchJoin()
            .where(qSchedule.userEmail.eq(userEmail)
                    .and((qSchedule.text.like("%"+key+"%")
                            .or(qPet.petName.like("%"+key+"%")))))
            .orderBy(qSchedule.scheduleTime.asc())
            .fetch();
    List<ScheduleResponseDTO> resultList = scheduleList.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());

    List<RecurringSchedule> recurringScheduleList = jpaQueryFactory
            .selectFrom(qRecurringSchedule)
            .innerJoin(qRecurringSchedule.pet,qPet).fetchJoin()
            .where(qRecurringSchedule.userEmail.eq(userEmail)
                    .and(qRecurringSchedule.text.like("%"+key+"%")
                            .or(qPet.petName.like("%"+key+"%"))))
            .fetch();
    resultList.addAll(recurringScheduleList.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList()));
    return resultList;
  }

  @Transactional
  public String addSchedule(ScheduleRequestDTO scheduleRequestDTO, String token) {
    Pet pet = petRepository.findById(scheduleRequestDTO.getPetId())
            .orElseThrow(() -> {throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});

    String userEmail = feignService.getUserEmail(token);
    if (scheduleRequestDTO.isHasRepeat()) {
      RecurringSchedule recurringSchedule = new RecurringSchedule(pet,userEmail,scheduleRequestDTO);
      recurringScheduleRepository.save(recurringSchedule);
      return "일정 추가 완료";
    }
    Schedule schedule = new Schedule(pet,userEmail,scheduleRequestDTO);
    scheduleRepository.save(schedule);
    return "일정 추가 완료";
  }

  // Update
  @Transactional
  public String updateSchedule(
      Long id, ScheduleRequestDTO scheduleRequestDTO) {
    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    Pet pet =
        petRepository
            .findById(scheduleRequestDTO.getPetId())
            .orElseThrow(
                () -> {
                  throw new NotFoundException(
                      ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());
                });

    Schedule schedule =
        jpaQueryFactory
            .select(qSchedule)
            .from(qSchedule)
            .innerJoin(qSchedule.pet, qPet)
            .where(qSchedule.id.eq(id))
            .fetchOne(); // 단일 건수의 데이터 조회 둘 이상일 경우 NonUniqueResultException

    schedule.update(scheduleRequestDTO, pet);
    scheduleRepository.save(schedule);
    return "수정 완료";
  }

  // Delete
  @Transactional
  public String deleteSchedule(Long id) {

    scheduleRepository.deleteById(id);
    return "삭제 완료";
  }
}
