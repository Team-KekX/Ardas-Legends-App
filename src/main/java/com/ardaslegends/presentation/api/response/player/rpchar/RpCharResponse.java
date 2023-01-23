package com.ardaslegends.presentation.api.response.player.rpchar;

import com.ardaslegends.domain.RPChar;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Schema(name = "RpChar", description = "RpChar response containing basic information")
public record RpCharResponse(
        @Schema(description = "Character's name", example = "Aragorn")
        String name,
        @Schema(description = "Character's title", example = "King of Gondor")
        String title,
        @Schema(description = "Character's gear as plain text", example = "Gondorian gear")
        String gear,
        @Schema(description = "If the character participates in pvp", example = "true")
        Boolean pvp
) {

    public RpCharResponse(RPChar rpChar) {
        this(rpChar.getName(), rpChar.getTitle(), rpChar.getGear(), rpChar.getPvp());
        log.debug("Created RpCharResponse");
    }
}
