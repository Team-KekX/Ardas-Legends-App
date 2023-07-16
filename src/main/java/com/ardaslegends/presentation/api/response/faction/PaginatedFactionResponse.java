package com.ardaslegends.presentation.api.response.faction;

import com.ardaslegends.domain.ArmyType;
import com.ardaslegends.domain.Faction;

public record PaginatedFactionResponse(
        long id,
        String nameOfFaction,
        String leaderIgn,
        String homeRegion,
        int countOfArmies,
        int countOfClaimbuilds,
        int countOfClaimedRegions,
        int countOfPlayers
) {
    public PaginatedFactionResponse(Faction faction) {
        this(
                faction.getId(),
                faction.getName(),
                faction.getLeader() != null ? faction.getLeader().getIgn() : null,
                faction.getHomeRegion().getId(),
                (int) faction.getArmies().stream().filter(army -> ArmyType.ARMY.equals(army.getArmyType())).count(),
                faction.getClaimBuilds().size(),
                faction.getRegions().size(),
                faction.getPlayers().size()
        );
    }
}
