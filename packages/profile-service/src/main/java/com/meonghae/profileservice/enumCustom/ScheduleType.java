package com.meonghae.profileservice.enumCustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleType {
    Test(-1,"테스트 일정",0),
    Custom(0,"커스텀 일정",0),
    //유저
    BirthDay(1,"특별한 날인 오늘, 생일 축하드려요!",12),
    PetBirthDay(2,"반려동물의 생일인 오늘, 행복한 시간되세요!",12),
    //예방접종
    DHPPL(11,"강아지혼합예방주사",12),
    CVRP(12,"고양이혼합예방주사",12),
    Coronavirus(13,"코로나바이러스성 장염",12),
    Rabies(14, "광견병",6),
    KennelCough(15,"기관지염",12),
    FelineLeukemia(16,"고양이 백혈병",12),
    FIP(17,"전염성 복막염",12),
    //구충제
    HeartWorm(21,"심장사상충",1),
    InternalParasitic(22,"내부기생충",1),
    ExternalParasites(23,"외부기생충",2),
    //건강검진
    HealthScreenings(31,"건강검진",12);


    private final int key;
    private final String title;
    private final int repeatCycle;

}
