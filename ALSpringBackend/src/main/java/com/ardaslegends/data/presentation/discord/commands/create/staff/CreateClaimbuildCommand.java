package com.ardaslegends.data.presentation.discord.commands.create.staff;

import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.ALStaffCommand;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

public class CreateClaimbuildCommand implements ALCommandExecutor, ALStaffCommand, DiscordUtils {
    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction) {
        return null;
    }
}
