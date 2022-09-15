package com.ardaslegends.data.presentation.discord.commands;

import com.ardaslegends.data.presentation.discord.commands.bind.Bind;
import com.ardaslegends.data.presentation.discord.commands.create.Create;
import com.ardaslegends.data.presentation.discord.commands.register.Register;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.presentation.discord.exception.BotException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter

@Slf4j
@Component
public class Commands implements DiscordUtils {

    private final DiscordApi api;
    private final Bind bind;
    private final Register register;
    private final Create create;

    private final Map<String, ALCommandExecutor> executions;

    public Commands(DiscordApi api, Bind bind, Register register, Create create) {
        this.api = api;
        this.bind = bind;
        this.register = register;
        this.create = create;

        executions = new HashMap<>();
        bind.init(executions);
        register.init(executions);
        create.init(executions);

        api.addSlashCommandCreateListener(event -> {

            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            String fullname = getFullCommandName(interaction);
            log.info("Incoming '/{}' command", fullname);

            interaction.respondLater().thenAccept(responseUpdater -> {
                EmbedBuilder embed = null;

                try {
                    log.trace("Calling command execution function");
                    embed = executions.get(fullname).execute(interaction);

                    log.debug("Updating response to new embed");
                    responseUpdater.addEmbed(embed).update();
                    log.info("Finished handling '/{}' command", fullname);
                }
                catch (BotException exception) {
                    log.warn("Encountered ServiceException while executing, msg: {}", exception.getMessage());
                    embed = createErrorEmbed(exception.getTitle(), exception.getMessage());
                }
                catch (Exception exception) {
                    log.error("ENCOUNTERED UNEXPECTED ERROR OF TYPE {} - MSG: {}", exception.getClass(), exception.getMessage());
                    String message = exception.getMessage() + "\nPlease contact the devs!";
                    embed = createErrorEmbed("An unexpected error occured", message);
                }

            });
        });
    }

}
