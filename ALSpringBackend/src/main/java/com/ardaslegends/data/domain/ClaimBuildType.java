package com.ardaslegends.data.domain;

public enum ClaimBuildType {

    // maxArmies, maxTradingCompanies, freeArmies, freeTradingCompanies
    HAMLET(0,0,0,0, "Hamlet"),
    VILLAGE(0,0,0,0, "Village"),
    TOWN(1,1,1,1, "Town"),
    CAPITAL(2,1,2,1, "Capital"),
    KEEP(0,0,0,0, "Keep"),
    CASTLE(1,0,1,0, "Castle"),
    STRONGHOLD(1,0,1,0, "Stronghold");

    private final int maxArmies;
    private final int freeArmies;
    private final int maxTradingCompanies;
    private final int freeTradingCompanies;
    private final String name;
    private ClaimBuildType(int maxArmies, int maxTradingCompanies, int freeArmies, int freeTradingCompanies, String name) {
        this.maxArmies = maxArmies;
        this.maxTradingCompanies = maxTradingCompanies;
        this.freeArmies = freeArmies;
        this.freeTradingCompanies = freeTradingCompanies;
        this.name = name;
    }

    public int getMaxArmies() {return maxArmies; }
    public int getMaxTradingCompanies() {return maxTradingCompanies; }
    public int getFreeArmies() {return freeArmies; }
    public int getFreeTradingCompanies() {return freeTradingCompanies; }
    public String getName() {
        return this.name;
    }

}
