package com.ardaslegends.alspringbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class ProductionSite {

    private final Long id;
    private final ProductionSiteType type; //unique, type of production site, e.g. FARM
    private final String producedResource; //the resource this production site produces
    private final Integer amount; //the amount

}
