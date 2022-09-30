package com.ardaslegends.service.dto.army;

import com.ardaslegends.domain.ArmyType;
import com.ardaslegends.service.dto.unit.UnitTypeDto;

public record CreateArmyDto(
        String executorDiscordId,
         String name,
         ArmyType armyType,
         String claimBuildName,
         UnitTypeDto[] units,
         String unitString) {
    public CreateArmyDto(String executorDiscordId, String name, ArmyType armyType, String claimBuildName, UnitTypeDto[] units, String unitString) {
        this.executorDiscordId = executorDiscordId;
        this.name = name;
        this.armyType = armyType;
        this.claimBuildName = claimBuildName;
        this.units = units;
        this.unitString = unitString;
    }

    public CreateArmyDto(String executorDiscordId, String name, ArmyType armyType, String claimBuildName, String unitString) {
        this(executorDiscordId,name,armyType,claimBuildName,null,unitString);
    }

    public CreateArmyDto(String executorDiscordId, String name, ArmyType armyType, String claimBuildName, UnitTypeDto[] units) {
        this(executorDiscordId,name,armyType,claimBuildName,units,null);
    }
}
