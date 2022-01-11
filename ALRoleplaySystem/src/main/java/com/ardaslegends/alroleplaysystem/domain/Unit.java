package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Unit {

    private UnitType unitType; //The kind of unit, e.g. Gondor Soldier
    private Integer count; //how many of those units are in the army

}
