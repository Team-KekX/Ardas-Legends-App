package com.ardaslegends.data.presentation.discord.commands.unbind;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.ArmyType;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.Thumbnails;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.BindArmyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class UnbindArmyOrCompanyCommand implements ALCommandExecutor {

    private final ArmyService armyService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /unbind army-or-company request");

        User user = interaction.getUser();
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        log.debug("Getting options");
        String armyName = getStringOption("army-or-company-name", options);
        log.debug("armyName: [{}]", armyName);
        User target = getUserOption("target-player", options);
        log.debug("target-player: [{}]", target.getDiscriminatedName());

        log.trace("Building dto");
        BindArmyDto dto = new BindArmyDto(user.getIdAsString(), target.getIdAsString(), armyName);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling armyService");
        Army army = discordServiceExecution(dto, armyService::unbind, "Error while unbinding from Army/Company");

        String armyType = army.getArmyType().getName();

        String thumbnail = "";
        if (army.getArmyType() == ArmyType.ARMY) {
            thumbnail = getFactionBanner(army.getFaction().getName());
        } else {
            thumbnail = Thumbnails.BIND_TRADER.getUrl();
        }

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Unbound from %s".formatted(armyType))
                .setDescription("The army %s has been unbound from player %s".formatted(army.getName(), target.getMentionTag()))
                .setColor(ALColor.GREEN)
                .addInlineField("Unbound User", target.getMentionTag())
                .addInlineField("Army", army.getName())
                .addInlineField("Army Faction", army.getFaction().getName())
                .addInlineField("Army's current Region", army.getCurrentRegion().getId())
                .setThumbnail(thumbnail)
                .setTimestampToNow();
    }
}
