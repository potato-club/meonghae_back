package com.meonghae.profileservice.enumCustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VaccinationType {

    DHPPL(1,"혼합백신"),
    Coronavirus(2,"코로나장염"),
    KennelCough(3,"전염성기관지염"),
    Rabies(4, "광견병"),
    HeartWorm(5,"심장사상충"),

    CVRP(6,"혼합예방주사"),
    FelineLeukemia(7,"고양이 백혈병"),
    FIP(8,"전염성 복막염");

    private final int key;
    private final String title;

}
