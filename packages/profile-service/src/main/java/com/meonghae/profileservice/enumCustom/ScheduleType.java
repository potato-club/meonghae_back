package com.meonghae.profileservice.enumCustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleType {
    //유저
    BirthDay(1,"특별한 날인 오늘, 생일 축하드려요!",12),
    PetBirthDay(2,"반려동물의 생일인 오늘, 행복한 시간되세요!",12),
    //강아지
    DHPPL(11,"혼합백신",12),
    Coronavirus(12,"코로나장염",12),
    KennelCough(13,"전염성기관지염",12),
    Rabies(14, "광견병",6),
    HeartWorm(15,"심장사상충",1),
    //고양이
    CVRP(21,"혼합예방주사",12),
    FelineLeukemia(22,"고양이 백혈병",12),
    FIP(23,"전염성 복막염",12);


    private final int key;
    private final String title;
    private final int repeatCycle;

}
