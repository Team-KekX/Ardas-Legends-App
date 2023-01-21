package com.ardaslegends.presentation.discord.commands.update.staff;

import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.FactionService;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor
public class UpdateFactionRoleCommand implements ALStaffCommandExecutor {

    private final FactionService factionService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming updateFactionRole request");

        checkStaff(interaction, properties.getStaffRoles());
        log.trace("UpdateFactionRole: User is staff, allowing update");

        var factionName = getStringOption("faction-name", options);
        log.trace("UpdateFactionRole: Faction is [{}]", factionName);

        var role = getRoleOption("role", options);
        log.trace("UpdateFactionRole: Role is [name: {}, id: {}]", role.getName(), role.getId());

        log.trace("UpdateFactionRole: Calling setFactionRoleId method");
        var faction = factionService.setFactionRoleId(factionName, role.getId());
        log.trace("UpdateFactionRole: Faction Role Id for faction [{}] has been set to [{}]", faction.getName(), faction.getFactionRoleId());

        log.debug("UpdateFactionRole: Building Embed");
        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Changed Faction Role")
                .setDescription("Staff changed the faction role of %s".formatted(faction.getName()))
                .setColor(ALColor.YELLOW)
                .addInlineField("Role", role.getMentionTag())
                .setThumbnail(getFactionBanner(factionName)));
    }
}
