package com.ardaslegends.presentation.api.response.applications;

import com.ardaslegends.domain.applications.RoleplayApplication;
import com.ardaslegends.presentation.api.response.player.PlayerResponse;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;

public record RoleplayApplicationResponse(
        long id,
        String playerIgn,
        String factionName,
        String characterName,
        String characterTitle,
        String characterReason,
        String gear,
        String linkToLore,
        OffsetDateTime appliedAt,
        PlayerResponse[] acceptedBy,
        short voteCount
) {
    public RoleplayApplicationResponse(RoleplayApplication application) {
        this(
                application.getId(),
                application.getApplicant().getIgn(),
                application.getApplicant().getFaction().getName(),
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleplayApplicationResponse that = (RoleplayApplicationResponse) o;
        return id == that.id && voteCount == that.voteCount && Objects.equals(playerIgn, that.playerIgn) && Objects.equals(factionName, that.factionName) && Objects.equals(characterName, that.characterName) && Objects.equals(characterTitle, that.characterTitle) && Objects.equals(characterReason, that.characterReason) && Objects.equals(gear, that.gear) && Objects.equals(linkToLore, that.linkToLore) && Objects.equals(appliedAt, that.appliedAt) && Arrays.equals(acceptedBy, that.acceptedBy);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, playerIgn, factionName, characterName, characterTitle, characterReason, gear, linkToLore, appliedAt, voteCount);
        result = 31 * result + Arrays.hashCode(acceptedBy);
        return result;
    }
    @Override
    public String toString() {
        return "RoleplayApplicationResponse{" +
                "id=" + id +
                ", playerIgn='" + playerIgn + '\'' +
                ", factionName='" + factionName + '\'' +
                ", characterName='" + characterName + '\'' +
                ", characterTitle='" + characterTitle + '\'' +
                ", characterReason='" + characterReason + '\'' +
                ", gear='" + gear + '\'' +
                ", linkToLore='" + linkToLore + '\'' +
                ", appliedAt=" + appliedAt +
                ", acceptedBy=" + Arrays.toString(acceptedBy) +
                ", voteCount=" + voteCount +
                '}';
    }
}
