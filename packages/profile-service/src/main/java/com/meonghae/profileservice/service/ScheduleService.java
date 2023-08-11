package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.schedule.SchedulePreviewResponseDto;
import com.meonghae.profileservice.dto.schedule.ScheduleRequestDTO;
import com.meonghae.profileservice.dto.schedule.ScheduleResponseDTO;
import com.meonghae.profileservice.dto.schedule.SimpleSchedule;
import com.meonghae.profileservice.entity.*;
import com.meonghae.profileservice.enumCustom.ScheduleCycleType;
import com.meonghae.profileservice.enumCustom.ScheduleType;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.ScheduleRepository;
import com.meonghae.profileservice.repository.PetRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
  private final JPAQueryFactory jpaQueryFactory;
  private final FeignService feignService;

  @Transactional
  public ScheduleResponseDTO getSchedule(Long id, String token) {
    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    Schedule schedule = (Schedule) jpaQueryFactory
            .selectFrom(qSchedule)
            .leftJoin(qSchedule.pet,qPet)
            .where(qSchedule.userEmail.eq(userEmail)
                    .and(qSchedule.id.eq(id)))
            .fetch();

    return new ScheduleResponseDTO(schedule);
  }

  // 프로필 화면에서 가까운 일정 순서대로 표시하기위함.
  @Transactional
  public List<SchedulePreviewResponseDto> getProfileSchedule(String token) {
    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    List<Schedule> scheduleList = jpaQueryFactory
            .selectFrom(qSchedule)
            .leftJoin(qSchedule.pet,qPet)
            .where(qSchedule.userEmail.eq(userEmail)
                    .and(qSchedule.scheduleEndTime.goe(LocalDateTime.now()))
                    .or(qSchedule.hasRepeat.isFalse()
                            .and(qSchedule.scheduleTime.goe(LocalDateTime.now()))))
            .fetch();

    List<SchedulePreviewResponseDto> resultList = new ArrayList<>();

    // 반복 일정을 가장 가까운 미래 일정으로 변환하여 allSchedules에 추가
    for ( Schedule schedule : scheduleList ) {
      if (!schedule.isHasRepeat()) {
        resultList.add(new SchedulePreviewResponseDto(schedule));
      }
      else {
        ChronoUnit unit = (schedule.getCycleType() == ScheduleCycleType.Month) ? ChronoUnit.MONTHS : ChronoUnit.DAYS;
        processRepeatSchedule(resultList, schedule, unit);
      }
    }
    // SchedulePreviewResponseDto.scheduleDate로 현재 날짜에 가까운 순으로 정렬
    resultList.sort(Comparator.comparing(SchedulePreviewResponseDto::getScheduleDate));

    // 상위 5개만 리턴
    return resultList.subList(0, Math.min(resultList.size(), 5));
  }


  //날짜 하나 클릭시 그 날짜에 대한 일정들 리턴
  @Transactional
  public List<ScheduleResponseDTO> getDayOfSchedule(
          LocalDate targetDate, String token, List<Long> scheduleId) {

    String userEmail = feignService.getUserEmail(token);
    QSchedule qSchedule = QSchedule.schedule;

    List<Schedule> scheduleList = jpaQueryFactory
            .selectFrom(qSchedule)
            .where(qSchedule.id.in(scheduleId).and(qSchedule.userEmail.eq(userEmail)))
            .fetch();

    List<ScheduleResponseDTO> resultList = new ArrayList<>();

    for (Schedule schedule : scheduleList) {
      resultList.add(new ScheduleResponseDTO(schedule,LocalDateTime.of(targetDate.getYear(),targetDate.getMonth(),schedule.getScheduleTime().getDayOfMonth(),schedule.getScheduleTime().getHour(),schedule.getScheduleTime().getMinute())));
    }
    return resultList;
  }

  // 달력 월단위 일정들 보기 위한 함수 - 같은 해 같은 월 데이터 출력
  @Transactional
  public List<SimpleSchedule> getMonthOfSchedule(LocalDate targetDate, String token) {

    String userEmail = feignService.getUserEmail(token);
    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    LocalDateTime monthEndPoint = targetDate.atStartOfDay().plusMonths(1) .minusDays(1);
    List<Schedule> scheduleList = jpaQueryFactory
            .selectFrom(qSchedule)
            .leftJoin(qSchedule.pet,qPet)
            .where(qSchedule.userEmail.eq(userEmail)
                    .and(qSchedule.scheduleEndTime.goe(targetDate.atStartOfDay()))
                    .or(qSchedule.hasRepeat.isFalse()
                            .and(qSchedule.scheduleTime.between(targetDate.atStartOfDay(),monthEndPoint))))
            .fetch();

    Map<Integer, SimpleSchedule> scheduleMap = new HashMap<>();

    for (Schedule schedule : scheduleList) {
      //일반 일정은 시작지점은 골랐으니 달 비교로 해당하는거만 넣고
      if (!schedule.isHasRepeat() && schedule.getScheduleTime().getMonthValue() == targetDate.getMonthValue()) {

        addSimpleSchedule(scheduleMap, schedule.getScheduleTime().getDayOfMonth(), schedule.getId().intValue());

      }
      //주기타입이 month 인 것
      else if (schedule.getCycleType() == ScheduleCycleType.Month) {
        if ((targetDate.getMonthValue() - schedule.getScheduleTime().getMonthValue()) % schedule.getCycle() == 0) {

          addSimpleSchedule(scheduleMap, schedule.getScheduleTime().getDayOfMonth(), schedule.getId().intValue());

        }
      }
      //반복 일정 && 타입이 커스텀이면서 주기타입이 day 인 것
      else if (schedule.isHasRepeat() && schedule.getScheduleType() == ScheduleType.Custom && schedule.getCycleType() == ScheduleCycleType.Day) {
        List<LocalDateTime> intendedTimeList = calculateRepeatedDays(schedule,targetDate);
        for (LocalDateTime intendedTime : intendedTimeList) {

          addSimpleSchedule(scheduleMap, intendedTime.getDayOfMonth(), schedule.getId().intValue());
        }
      } else throw new RuntimeException();
    }

    return new ArrayList<>(scheduleMap.values());
  }



  @Transactional
  public List<ScheduleResponseDTO> getScheduleOfFindByText(String key, String token){
    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    List<Schedule> scheduleList = jpaQueryFactory
            .selectFrom(qSchedule)
            .innerJoin(qSchedule.pet, qPet).fetchJoin()
            .where(qSchedule.userEmail.eq(userEmail)
                    .and((qSchedule.text.like("%"+key+"%")
                            .or(qPet.petName.like("%"+key+"%")))))
            .orderBy(qSchedule.scheduleTime.asc())
            .fetch();

    return scheduleList.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());
  }

  @Transactional
  public String addSchedule(ScheduleRequestDTO scheduleRequestDTO, String token) {
    Pet pet = petRepository.findById(scheduleRequestDTO.getPetId())
            .orElseThrow(() -> {throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});

    String userEmail = feignService.getUserEmail(token);
    LocalDateTime repeatEndTime = LocalDateTime.of(1,1,1,1,0);
    //반복하니? // 반복 안되는 것들은 다 위 기본값으로 들어감
    if (scheduleRequestDTO.isHasRepeat()) {
      //일정 타입비교
      if (scheduleRequestDTO.getScheduleType().equals(ScheduleType.Custom)) {
        switch (scheduleRequestDTO.getCycleType().getKey()) {
          //0이 달 1이 일 // 무한 반복설정 or 반복주기 * 반복횟수
          case 0 : repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,01,01,00,00)
                  : scheduleRequestDTO.getScheduleTime().plus(scheduleRequestDTO.getCycle() * scheduleRequestDTO.getCycleCount(), ChronoUnit.MONTHS);
          break;
          case 1 : repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,01,01,00,00)
                  : scheduleRequestDTO.getScheduleTime().plus(scheduleRequestDTO.getCycle() * scheduleRequestDTO.getCycleCount(), ChronoUnit.DAYS);
          break;
        }

      } else {// 무한 반복설정 or 반복주기 * 반복횟수
        repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,01,01,00,00)
                : scheduleRequestDTO.getScheduleTime().plus(scheduleRequestDTO.getScheduleType().getRepeatCycle() * scheduleRequestDTO.getCycleCount(), ChronoUnit.MONTHS);
        scheduleRequestDTO.setCycleType(ScheduleCycleType.Month);
        scheduleRequestDTO.setCycle(scheduleRequestDTO.getScheduleType().getRepeatCycle());
      }
    }


    Schedule schedule = new Schedule(pet,userEmail,repeatEndTime,scheduleRequestDTO);
    scheduleRepository.save(schedule);
    return "일정 추가 완료";
  }

  // Update
  @Transactional
  public String updateSchedule(
      Long id, ScheduleRequestDTO scheduleRequestDTO) {
    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    Pet pet = petRepository.findById(scheduleRequestDTO.getPetId()).orElseThrow(() -> {throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});

    Schedule schedule =
        jpaQueryFactory
            .select(qSchedule)
            .from(qSchedule)
            .innerJoin(qSchedule.pet, qPet)
            .where(qSchedule.id.eq(id))
            .fetchOne(); // 단일 건수의 데이터 조회 둘 이상일 경우 NonUniqueResultException

    LocalDateTime repeatEndTime = LocalDateTime.of(1,1,1,1,0);
    //반복하니? // 반복 안되는 것들은 다 위 기본값으로 들어감
    if (scheduleRequestDTO.isHasRepeat()) {
      //일정 타입비교
      if (scheduleRequestDTO.getScheduleType().equals(ScheduleType.Custom)) {
        switch (scheduleRequestDTO.getCycleType().getKey()) {
          //0이 달 1이 일 // 무한 반복설정 or 반복주기 * 반복횟수
          case 0 : repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,01,01,00,00)
                  : scheduleRequestDTO.getScheduleTime().plus(scheduleRequestDTO.getCycle() * scheduleRequestDTO.getCycleCount(), ChronoUnit.MONTHS);
            break;
          case 1 : repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,01,01,00,00)
                  : scheduleRequestDTO.getScheduleTime().plus(scheduleRequestDTO.getCycle() * scheduleRequestDTO.getCycleCount(), ChronoUnit.DAYS);
            break;
        }

      } else {// 무한 반복설정 or 반복주기 * 반복횟수
        repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,01,01,00,00)
                : scheduleRequestDTO.getScheduleTime().plus(scheduleRequestDTO.getScheduleType().getRepeatCycle() * scheduleRequestDTO.getCycleCount(), ChronoUnit.MONTHS);
        scheduleRequestDTO.setCycleType(ScheduleCycleType.Month);
        scheduleRequestDTO.setCycle(scheduleRequestDTO.getScheduleType().getRepeatCycle());
      }
    }
    schedule.update(scheduleRequestDTO, pet, repeatEndTime);
    return "수정 완료";
  }

  // Delete
  @Transactional
  public String deleteSchedule(Long id) {

    scheduleRepository.deleteById(id);
    return "삭제 완료";
  }

 //=========================================================

  private void addSimpleSchedule(Map<Integer, SimpleSchedule> scheduleMap, int day, int scheduleId) {
    SimpleSchedule simpleSchedule = scheduleMap.getOrDefault(day, new SimpleSchedule());
    simpleSchedule.setDay(day);
    if (simpleSchedule.getScheduleIds() == null) {
      simpleSchedule.setScheduleIds(new ArrayList<>());
    }
    simpleSchedule.getScheduleIds().add(scheduleId);
    scheduleMap.put(day, simpleSchedule);
  }

  // 날짜 관련 유틸리티 메서드
  private List<LocalDateTime> calculateRepeatedDays(Schedule schedule, LocalDate targetDate) {
    List<LocalDateTime> recurringDates = new ArrayList<>();

    LocalDateTime nextScheduleTime = schedule.getScheduleTime();
    // 목표 날짜와 시작 날짜 사이의 차이 계산 / 필요한 반복 횟수 계산
    long repeatCount = (ChronoUnit.DAYS.between(nextScheduleTime.toLocalDate(), targetDate)) / schedule.getCycle();

    nextScheduleTime = nextScheduleTime.plusDays(repeatCount * schedule.getCycle());

    // 해당 월의 마지막 날짜까지 반복
    while (nextScheduleTime.toLocalDate().isBefore(targetDate.plusMonths(1))) {
      if (nextScheduleTime.getMonthValue() == targetDate.getMonthValue()) {
        recurringDates.add(nextScheduleTime);
      }
      nextScheduleTime = nextScheduleTime.plusDays(schedule.getCycle());
    }

    return recurringDates;
  }

  private void processRepeatSchedule(List<SchedulePreviewResponseDto> resultList, Schedule schedule, ChronoUnit unit) {
    LocalDateTime nextScheduleTime = schedule.getScheduleTime();
    int repeatCount = (int) (unit.between(nextScheduleTime, LocalDateTime.now()) / schedule.getCycle());
    if (repeatCount == 0) {
      repeatCount = nextScheduleTime.isBefore(LocalDateTime.now()) ? 1 : 0;
    }
    nextScheduleTime = nextScheduleTime.plus(repeatCount * schedule.getCycle(), unit);

    for (int i = 0; i < 5; i++) {
      resultList.add(new SchedulePreviewResponseDto(schedule, nextScheduleTime));
      nextScheduleTime = nextScheduleTime.plus(schedule.getCycle(), unit);
      if (schedule.getScheduleEndTime().isBefore(nextScheduleTime)) {
        break;
      }
    }
  }
}
