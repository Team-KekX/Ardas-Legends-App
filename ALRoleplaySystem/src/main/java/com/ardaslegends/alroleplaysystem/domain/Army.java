package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class Army {

    public String name; //unique, the army's name
    public ArmyType armyType; //type of the army, either ARMY, TRADING_COMPANY or ARMED_TRADERS
    public Faction faction; //the faction this army belongs to
    public Region currentRegion; //region the army is currently in
    public RPChar boundTo; //rp character the army is currently bound to
    public List<Unit> units; //the units in this army contains
    public List<String> sieges; //list of siege equipment this
    public ClaimBuild stationedAt; //claimbuild where this army is stationed
    public Integer freeTokens; //how many free unit tokens this army has left
    public ClaimBuild originalClaimbuild; //claimbuild where this army was created from

}
