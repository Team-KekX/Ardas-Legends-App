package com.ardaslegends.presentation.api.response.war;

import com.ardaslegends.domain.war.War;

import java.time.LocalDateTime;

public record PaginatedWarsResponse(
        String nameOfWar,
        String nameOfAttacker,
        String nameOfDefender,
        LocalDateTime startDate
) {
    public PaginatedWarsResponse(War war) {
        this(
                war.getName(),
                war.getInitialAttacker().getWarParticipant().getName(),
                war.getInitialDefender().getWarParticipant().getName(),
                war.getStartDate()
        );
    }
}
