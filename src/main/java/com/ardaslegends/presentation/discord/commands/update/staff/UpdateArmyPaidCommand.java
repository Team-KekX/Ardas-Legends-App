package com.ardaslegends.presentation.discord.commands.update.staff;

import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.ArmyService;
import com.ardaslegends.service.dto.army.UpdateArmyDto;
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
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
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

        String description = army.getIsPaid()
                ? "Setting isPaid of Army " + army.getName() + "to true. \nThis indicates that the army has been paid for!"
                : "Setting isPaid of Army " + army.getName() + "to false.\nThis indicates that the creation of the army still needs to be paid for!";

        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Payment received!")
                .setDescription(description)
                .addInlineField("Army", army.getName())
                .addInlineField("IsPaid", army.getIsPaid().toString())
                .setColor(ALColor.YELLOW)
                .setTimestampToNow());

    }
}
