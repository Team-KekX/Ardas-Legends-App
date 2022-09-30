package com.ardaslegends.domain;

public enum ArmyType {
    ARMY("Army"),
    TRADING_COMPANY("Trading Company"),
    ARMED_TRADERS("Armed Trading Company");

    private final String name;
    ArmyType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
