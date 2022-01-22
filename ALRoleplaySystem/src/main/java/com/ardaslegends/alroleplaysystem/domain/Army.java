package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor

public class Army {

    private String name; //unique, the army's name
    private ArmyType armyType; //type of the army, either ARMY, TRADING_COMPANY or ARMED_TRADERS
    private Faction faction; //the faction this army belongs to
    private Region currentRegion; //region the army is currently in
    private RPChar boundTo; //rp character the army is currently bound to
    private List<Unit> units; //the units in this army contains
    private List<String> sieges; //list of siege equipment this
    private ClaimBuild stationedAt; //claimbuild where this army is stationed
    private Integer freeTokens; //how many free unit tokens this army has left
    private ClaimBuild originalClaimbuild; //claimbuild where this army was created from

    public void setCurrentRegion(Region currentRegion) {
        this.currentRegion = currentRegion;
    }

    public void setBoundTo(RPChar boundTo) {
        this.boundTo = boundTo;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public void setSieges(List<String> sieges) {
        this.sieges = sieges;
    }

    public void setStationedAt(ClaimBuild stationedAt) {
        this.stationedAt = stationedAt;
    }

    public void setFreeTokens(Integer freeTokens) {
        this.freeTokens = freeTokens;
    }
}
