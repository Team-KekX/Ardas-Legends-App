package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class UnitType {

    public String unitName; //unique, the name of this Unit
    public Integer tokenCost; //how much tokens this unit costs

}
