package com.ardaslegends.data.domain;

public enum SpecialBuilding {
    WATCHTOWER("Watchtower"),
    HOUSE_OF_HEALING("House of Healing"),
    EMBASSY("Embassy"),
    HARBOUR("Harbour"),
    STABLES("Stables"),
    BANK("Bank"),
    INN("Inn"),
    MARKET("Market"),
    SHOP("Shop"),
    WALL("Wall");

    private String name;

    private SpecialBuilding(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
