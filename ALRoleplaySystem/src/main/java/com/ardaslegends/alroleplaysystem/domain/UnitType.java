package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class UnitType {

    private final String unitName; //unique, the name of this Unit
    private final Integer tokenCost; //how much tokens this unit costs

}
