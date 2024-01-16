package com.ardaslegends.presentation.api.response.war;

import com.ardaslegends.domain.war.War;

import java.time.OffsetDateTime;

public record ActiveWarResponse(
        String warName,

        WarParticipantResponse[] aggressors,

        WarParticipantResponse[] defenders,

        OffsetDateTime startDate,

        BattleResponse[] battles
) {
    public ActiveWarResponse(War war) {
        this(
                war.getName(),

                war.getAggressors().stream()
                        .map(WarParticipantResponse::new)
                        .toArray(WarParticipantResponse[]::new),

                war.getDefenders().stream()
                        .map(WarParticipantResponse::new)
                        .toArray(WarParticipantResponse[]::new),

                war.getStartDate(),

                war.getBattles().stream()
                        .map(BattleResponse::new)
                        .toArray(BattleResponse[]::new)
        );
    }
}
