package com.ardaslegends.service.dto.player;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateDiscordIdDto(

        @Schema(description = "old Discord ID", example = "1015367754771083405")
        String oldDiscordId,
        @Schema(description = "new Discord ID", example = "261173268365443074")
        String newDiscordId
) { }
