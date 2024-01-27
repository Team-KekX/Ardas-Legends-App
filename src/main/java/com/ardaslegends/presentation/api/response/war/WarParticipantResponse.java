package com.ardaslegends.presentation.api.response.war;

import com.ardaslegends.domain.war.WarParticipant;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record WarParticipantResponse(
        String factionName,
        boolean initialParty,
        OffsetDateTime joinedWarAt
) {
    public WarParticipantResponse(WarParticipant participant) {
        this(
                participant.getWarParticipant().getName(),
                participant.getInitialParty(),
                participant.getJoiningDate()
        );
    }
}
