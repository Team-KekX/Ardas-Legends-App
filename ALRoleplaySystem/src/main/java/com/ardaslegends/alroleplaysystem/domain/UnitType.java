package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class UnitType {

    private String unitName; //unique, the name of this Unit
    private Integer tokenCost; //how much tokens this unit costs

}
