package com.ardaslegends.domain;

import com.ardaslegends.configuration.converter.ProductionSiteTypeEnumConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(converter = ProductionSiteTypeEnumConverter.class)
public enum ProductionSiteType {
    FARM("Farm"),
    FISHING_LODGE("Fishing Lodge"),
    MINE("Mine"),
    QUARRY("Quarry"),
    MAN_FLESH_PIT("Manflesh Pit"),
    SLAUGHTERHOUSE("Slaughterhouse"),
    HUNTING_LODGE("Hunting Lodge"),
    ORCHARD("Orchard"),
    LUMBER_CAMP("Lumber Camp"),
    WORKSHOP("Workshop"),
    INCOME("Income"),
    PEARL_FISHER("Pearl Fisher"),
    HOUSE_OF_LORE("House of Lore"),
    DYE_HOUSE("Dye House");

    private String name;

    private ProductionSiteType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
