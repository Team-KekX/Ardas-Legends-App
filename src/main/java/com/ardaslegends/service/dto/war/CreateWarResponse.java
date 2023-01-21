package com.ardaslegends.service.dto.war;

import com.ardaslegends.domain.Faction;
import org.javacord.api.entity.permission.Role;

public record CreateWarResponse(
        String warName,
        Faction aggressor,
        Role aggressorRole,
        Faction defender,
        Role defenderRole
) {
}
