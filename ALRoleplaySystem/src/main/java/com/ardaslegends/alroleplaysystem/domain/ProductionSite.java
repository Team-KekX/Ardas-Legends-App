package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ProductionSite {
    private ProductionSiteType type; //unique, type of production site, e.g. FARM
    private String producedResource; //the resource this production site produces
    private Integer amount; //the amount
}
