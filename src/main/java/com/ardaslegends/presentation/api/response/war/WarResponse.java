package com.ardaslegends.presentation.api.response.war;

import com.ardaslegends.domain.war.War;

import java.time.OffsetDateTime;

public record WarResponse(
        long id,
        String nameOfWar,
        String nameOfAttacker,
        String nameOfDefender,
        OffsetDateTime startDate
) {
    public WarResponse(War war) {
        this(
                war.getId(),
                war.getName(),
                war.getInitialAttacker().getWarParticipant().getName(),
                war.getInitialDefender().getWarParticipant().getName(),
                war.getStartDate()
        );
    }
}
