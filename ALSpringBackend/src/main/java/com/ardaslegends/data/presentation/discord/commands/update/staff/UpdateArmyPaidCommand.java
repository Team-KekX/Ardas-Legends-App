package com.ardaslegends.data.presentation.discord.commands.update.staff;

import com.ardaslegends.data.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.UpdateArmyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Component
public class UpdateArmyPaidCommand implements ALStaffCommandExecutor {

    private final ArmyService armyService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Handling /update army paid command, fetching option-data");

        checkStaff(interaction, properties.getStaffRoles());

        String armyName = getStringOption("army", options);
        log.trace("UpdateArmyPaid: Army name is [{}]", armyName);

        Boolean isPaid = getBooleanOption("paid", options);
        log.trace("UpdateArmyPaid: Paid attribute is [{}]", isPaid);

        log.trace("UpdateArmyPaid: Building Dto");
        UpdateArmyDto dto = new UpdateArmyDto(null, armyName, null, isPaid);

        log.debug("UpdateArmyPaid: Calling Service Execution");
        var army = discordServiceExecution(dto, armyService::setIsPaid, "Error while updating army paid attribute");

        return new EmbedBuilder()
                .setTitle("Payment received!")
                .setDescription("'IsPaid' of army '%s' has been set to '%s'".formatted(army.getName(), army.getIsPaid()))
                .addInlineField("Army", army.getName())
                .addInlineField("IsPaid", army.getIsPaid().toString())
                .setColor(ALColor.YELLOW)
                .setTimestampToNow();

    }
}
