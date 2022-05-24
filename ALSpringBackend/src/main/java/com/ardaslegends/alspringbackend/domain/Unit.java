package com.ardaslegends.alspringbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class Unit {

    private final UnitType unitType; //The kind of unit, e.g. Gondor Soldier
    private Integer count; //how many of those units are in the army

    public void setCount(Integer count) {
        this.count = count;
    }

}
