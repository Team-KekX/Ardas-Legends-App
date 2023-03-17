package com.ardaslegends.presentation.api.response.applications;

import com.ardaslegends.domain.applications.RoleplayApplication;
import com.ardaslegends.presentation.api.response.player.PlayerResponse;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
public record RoleplayApplicationResponse(
        long id,
        String playerIgn,
        String factionName,
        String characterName,
        String characterTitle,
        String characterReason,
        String gear,
        String linkToLore,
        LocalDateTime appliedAt,
        PlayerResponse[] acceptedBy,
        short voteCount
) {
    public RoleplayApplicationResponse(RoleplayApplication application) {
        this(
                application.getId(),
                application.getPlayer().getIgn(),
                application.getPlayer().getFaction().getName(),
                application.getCharacterName(),
                application.getCharacterTitle(),
                application.getWhyDoYouWantToBeThisCharacter(),
                application.getGear(),
                application.getLinkToLore(),
                application.getAppliedAt(),
                application.getAcceptedBy().stream().map(PlayerResponse::new).toArray(PlayerResponse[]::new),
                application.getVoteCount()
        );
    }
}
