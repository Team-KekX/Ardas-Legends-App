package com.ardaslegends.presentation.api.response.war;

import com.ardaslegends.domain.war.WarParticipant;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WarParticipantResponse(
        String factionName,
        boolean initialParty,
        LocalDateTime joinedWarAt
) {
    public WarParticipantResponse(WarParticipant participant) {
        this(
                participant.getWarParticipant().getName(),
                participant.isInitialParty(),
                participant.getJoiningDate()
        );
    }
}
