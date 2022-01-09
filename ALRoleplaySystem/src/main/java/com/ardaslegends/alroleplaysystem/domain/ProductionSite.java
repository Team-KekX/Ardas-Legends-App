package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ProductionSite {
    public ProductionSiteType type; //unique, type of production site, e.g. FARM
    public String producedResource; //the resource this production site produces
    public Integer amount; //the amount
}
