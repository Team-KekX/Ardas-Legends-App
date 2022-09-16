package com.ardaslegends.data.presentation.discord.commands;

import com.ardaslegends.data.presentation.discord.commands.bind.Bind;
import com.ardaslegends.data.presentation.discord.commands.create.CreateCommand;
import com.ardaslegends.data.presentation.discord.commands.register.RegisterCommand;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.presentation.discord.exception.BotException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter

@Slf4j
@Component
public class Commands implements DiscordUtils {

    private final DiscordApi api;
    private final Bind bind;
    private final RegisterCommand register;
    private final CreateCommand create;

    private final Map<String, ALCommandExecutor> executions;

    public Commands(DiscordApi api, Bind bind, RegisterCommand register, CreateCommand create) {
        this.api = api;
        this.bind = bind;
        this.register = register;
        this.create = create;

        executions = new HashMap<>();
        bind.init(executions);
        register.init(executions);
        create.init(executions);

        log.debug("Fetching roleplay-commands channel with ID in Property file");
        Channel rpCommandsChannel = api.getChannelById(BotProperties.rpCommandsChannel).orElseThrow();

        api.addSlashCommandCreateListener(event -> {

            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            String fullname = getFullCommandName(interaction);


            log.info("Incoming '/{}' command", fullname);

            try {

                var responseUpdater = interaction.respondLater().join();

                EmbedBuilder embed;
                try {
                    log.trace("Calling command execution function");
                    embed = executions.get(fullname).execute(interaction);

                    log.info("Finished handling '/{}' command", fullname);
                } catch (BotException exception) {
                    log.warn("Encountered ServiceException while executing, msg: {}", exception.getMessage());
                    embed = createErrorEmbed(exception.getTitle(), exception.getMessage());
                } catch (Exception exception) {
                    log.error("ENCOUNTERED UNEXPECTED ERROR OF TYPE {} - MSG: {}", exception.getClass(), exception.getMessage());
                    String message = exception.getMessage() + "\nPlease contact the devs!";
                    embed = createErrorEmbed("An unexpected error occured", message);
                }

                log.debug("Updating response to new embed");
                // The join() is important so that the exceptions go into the catch blocks
                responseUpdater.addEmbed(embed).update().join();
            } catch (Exception e) {
                // TODO: Create Error Report of Stacktrace and stuff, until then we're throwing this again
                // TODO: This does not
                e.printStackTrace();
            }
        });
    }

}
