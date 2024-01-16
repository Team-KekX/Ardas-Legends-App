package com.ardaslegends.presentation.api.response.war;

import com.ardaslegends.domain.war.War;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Objects;

public record PaginatedWarsResponse(
        long id,
        String nameOfWar,
        String nameOfAttacker,
        String nameOfDefender,
        OffsetDateTime startDate
) {
    public PaginatedWarsResponse(War war) {
        this(
                war.getId(),
                war.getName(),
                war.getInitialAttacker().getWarParticipant().getName(),
                war.getInitialDefender().getWarParticipant().getName(),
                war.getStartDate()
        );
    }
}
