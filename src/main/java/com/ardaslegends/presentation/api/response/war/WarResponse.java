package com.ardaslegends.presentation.api.response.war;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.domain.war.WarParticipant;

import java.time.OffsetDateTime;

public record WarResponse(
        long id,
        String name,
        String initialAttacker,
        String initialDefender,
        String[] agressors,
        String[] defenders,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        boolean isActive
) {
    public WarResponse(War war) {
        this(
                war.getId(),
                war.getName(),
                war.getInitialAttacker().getWarParticipant().getName(),
                war.getInitialDefender().getWarParticipant().getName(),
                war.getAggressors().stream()
                        .map(WarParticipant::getWarParticipant)
                        .map(Faction::getName)
                        .toArray(String[]::new),
                war.getDefenders().stream()
                        .map(WarParticipant::getWarParticipant)
                        .map(Faction::getName)
                        .toArray(String[]::new),
                war.getStartDate(),
                war.getEndDate(),
                war.getIsActive()
        );
    }
}
