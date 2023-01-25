package com.ardaslegends.presentation.api.response.war;

import java.time.LocalDateTime;

public record ActiveWarResponse(
        String warName,

        WarParticipantResponse[] aggressors,

        WarParticipantResponse[] defenders,

        LocalDateTime startDate


) {
}
