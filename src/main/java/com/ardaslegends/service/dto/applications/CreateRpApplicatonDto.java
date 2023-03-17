package com.ardaslegends.service.dto.applications;

public record CreateRpApplicatonDto(
        String discordId,
        String factionName,
        String characterName,
        String characterTitle,
        String characterReason,
        String gear,
        String linkToLore
) {
}
