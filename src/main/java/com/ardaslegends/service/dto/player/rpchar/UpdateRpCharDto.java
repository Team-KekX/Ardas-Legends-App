package com.ardaslegends.service.dto.player.rpchar;

public record UpdateRpCharDto(String discordId, String charName, String title, String currentRegion, String boundArmy, String gear, Boolean pvp) {
}
