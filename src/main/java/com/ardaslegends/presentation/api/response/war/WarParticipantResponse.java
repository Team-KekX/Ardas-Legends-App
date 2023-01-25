package com.ardaslegends.presentation.api.response.war;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WarParticipantResponse(
        String factionName,
        boolean initialParty,
        LocalDateTime joinedWarAt
) {
}
