package com.ardaslegends.presentation.api.response.faction;

import com.ardaslegends.domain.ArmyType;
import com.ardaslegends.domain.Faction;

public record PaginatedFactionResponse(
        long id,
        String nameOfFaction,
        int countOfArmies,
        int countOfClaimbuilds,
        int countOfClaimedRegions,
        int countOfPlayers
) {
    public PaginatedFactionResponse(Faction faction) {
        this(
                faction.getId(),
                faction.getName(),
                (int) faction.getArmies().stream().filter(army -> ArmyType.ARMY.equals(army)).count(),
                faction.getClaimBuilds().size(),
                faction.getRegions().size(),
                faction.getPlayers().size()
        );
    }
}
