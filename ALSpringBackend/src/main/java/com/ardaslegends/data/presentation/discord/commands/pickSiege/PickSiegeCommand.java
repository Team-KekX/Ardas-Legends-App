package com.ardaslegends.data.presentation.discord.commands.pickSiege;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.ArmyType;
import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.unstation.UnstationArmyOrCompanyCommand;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.Thumbnails;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.PickSiegeDto;
import com.ardaslegends.data.service.dto.army.StationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor

@Slf4j
@Component
public class PickSiegeCommand implements ALCommand, ALCommandExecutor {

    private final ArmyService armyService;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /pick-siege command");
        var command = SlashCommand.with("pick-siege", "Pick up siege equipment with an army", Arrays.asList(
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.STRING)
                        .setName("army-name")
                        .setDescription("Name of the army")
                        .setRequired(true)
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.STRING)
                        .setName("claimbuild-name")
                        .setDescription("The name of the claimbuild to pick up siege from")
                        .setRequired(true)
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.STRING)
                        .setName("siege")
                        .setDescription("The siege equipment to pick up")
                        .build()
                )
        );
        commands.put("register", this::execute);
        log.info("Finished initializing /unstation command");
        return command;
    }

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /pick-siege request");

        User user = interaction.getUser();
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        log.debug("Getting options");
        String armyName = getStringOption("army-name", options);
        log.debug("armyName: [{}]", armyName);
        String cbName = getStringOption("claimbuild-name", options);
        log.debug("claimbuild-name: [{}]", armyName);
        String siege = getStringOption("siege", options);
        log.debug("siege-name: [{}]", armyName);

        log.trace("Building dto");
        PickSiegeDto dto = new PickSiegeDto(user.getIdAsString(), armyName, cbName, siege);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling armyService");
        Army army = discordServiceExecution(dto, armyService::pickSiege, "Error while picking siege");

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Army picked up siege")
                .setDescription("The army %s has has picked up following siege from Claimbuild %s: %s".formatted(army.getName(), cbName, siege))
                .setColor(ALColor.GREEN)
                .addInlineField("Army", army.getName())
                .addInlineField("Army Faction", army.getFaction().getName())
                .addInlineField("Bound Player", army.getBoundTo() == null ? "None" : army.getBoundTo().getIgn())
                .addInlineField("Region", army.getCurrentRegion().getId())
                .addInlineField("Siege", String.join(", ", army.getSieges()))
                .setThumbnail(getFactionBanner(army.getFaction().getName()))
                .setTimestampToNow();
    }
}
