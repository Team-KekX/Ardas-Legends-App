package com.ardaslegends.data.presentation.discord.commands;

import com.ardaslegends.data.presentation.discord.commands.bind.BindCommand;
import com.ardaslegends.data.presentation.discord.commands.cancel.CancelCommand;
import com.ardaslegends.data.presentation.discord.commands.create.CreateCommand;
import com.ardaslegends.data.presentation.discord.commands.delete.DeleteCommand;
import com.ardaslegends.data.presentation.discord.commands.disband.DisbandCommand;
import com.ardaslegends.data.presentation.discord.commands.heal.HealCommand;
import com.ardaslegends.data.presentation.discord.commands.info.InfoCommand;
import com.ardaslegends.data.presentation.discord.commands.injure.InjureCommand;
import com.ardaslegends.data.presentation.discord.commands.move.MoveCommand;
import com.ardaslegends.data.presentation.discord.commands.register.RegisterCommand;
import com.ardaslegends.data.presentation.discord.commands.station.StationCommand;
import com.ardaslegends.data.presentation.discord.commands.unbind.UnbindCommand;
import com.ardaslegends.data.presentation.discord.commands.unstation.UnstationCommand;
import com.ardaslegends.data.presentation.discord.commands.update.UpdateCommand;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.presentation.discord.exception.BotException;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
public class Commands implements DiscordUtils {

    private final DiscordApi api;
    private final BindCommand bind;
    private final RegisterCommand register;
    private final CreateCommand create;
    private final DeleteCommand delete;
    private final InfoCommand info;
    private final Map<String, ALCommandExecutor> executions;

    private final BotProperties properties;
    public Commands(DiscordApi api, BindCommand bind, RegisterCommand register, CreateCommand create, DeleteCommand delete, BotProperties properties,
                    UpdateCommand update, MoveCommand move, CancelCommand cancel, InjureCommand injure, HealCommand heal, UnbindCommand unbind,
                    DisbandCommand disband, InfoCommand info, StationCommand station, UnstationCommand unstation
    ) {
        this.api = api;
        this.bind = bind;
        this.register = register;
        this.create = create;
        this.delete = delete;
        this.properties = properties;
        this.info = info;

        executions = new HashMap<>();
        List<SlashCommandBuilder> commands = new ArrayList<>();
        commands.add(bind.init(executions));
        commands.add(register.init(executions));
        commands.add(create.init(executions));
        commands.add(delete.init(executions));
        commands.add(update.init(executions));
        commands.add(move.init(executions));
        commands.add(cancel.init(executions));
        commands.add(injure.init(executions));
        commands.add(heal.init(executions));
        commands.add(unbind.init(executions));
        commands.add(disband.init(executions));
        commands.add(info.init(executions));
        commands.add(station.init(executions));
        commands.add(unstation.init(executions));

        api.bulkOverwriteGlobalApplicationCommands(commands).join();
        log.info("Updated [{}] global commands", commands.size());

        log.debug("Fetching roleplay-commands channel with ID in Property file");
        Channel rpCommandsChannel = api.getChannelById(properties.getRpCommandsChannel()).orElseThrow();

        api.addSlashCommandCreateListener(event -> {

            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            try {

                var responseUpdater = interaction.respondLater().join();

                EmbedBuilder embed;
                try {
                    String fullname = getFullCommandName(interaction);
                    log.trace("Full CommandName: [{}]", fullname);
                    List<SlashCommandInteractionOption> options = getOptions(interaction);

                    log.trace("List of available options: {}", options.stream()
                            .map(interactionOption -> interactionOption.getName())
                            .collect(Collectors.joining(", ")));

                    log.info("Incoming '/{}' command", fullname);
                    log.trace("Calling command execution function");
                    embed = executions.get(fullname).execute(interaction, options, properties);

                    log.info("Finished handling '/{}' command", fullname);
                } catch (BotException exception) {
                    log.warn("Encountered ServiceException while executing, msg: {}", exception.getMessage());
                    embed = createErrorEmbed(exception.getTitle(), exception.getMessage());
                } catch (Exception exception) {
                    log.error("ENCOUNTERED UNEXPECTED ERROR OF TYPE {} - MSG: {}", exception.getClass(), exception.getMessage());
                    exception.printStackTrace();
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
