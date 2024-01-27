package com.ardaslegends.presentation.discord.commands;

import com.ardaslegends.presentation.discord.commands.bind.BindCommand;
import com.ardaslegends.presentation.discord.commands.cancel.CancelCommand;
import com.ardaslegends.presentation.discord.commands.create.CreateCommand;
import com.ardaslegends.presentation.discord.commands.declare.DeclareCommand;
import com.ardaslegends.presentation.discord.commands.delete.DeleteCommand;
import com.ardaslegends.presentation.discord.commands.disband.DisbandCommand;
import com.ardaslegends.presentation.discord.commands.heal.HealCommand;
import com.ardaslegends.presentation.discord.commands.info.InfoCommand;
import com.ardaslegends.presentation.discord.commands.injure.InjureCommand;
import com.ardaslegends.presentation.discord.commands.move.MoveCommand;
import com.ardaslegends.presentation.discord.commands.pickSiege.PickSiegeCommand;
import com.ardaslegends.presentation.discord.commands.register.RegisterCommand;
import com.ardaslegends.presentation.discord.commands.remove.RemoveCommand;
import com.ardaslegends.presentation.discord.commands.station.StationCommand;
import com.ardaslegends.presentation.discord.commands.stockpile.StockpileCommand;
import com.ardaslegends.presentation.discord.commands.unbind.UnbindCommand;
import com.ardaslegends.presentation.discord.commands.unstation.UnstationCommand;
import com.ardaslegends.presentation.discord.commands.update.UpdateCommand;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.exception.BotException;
import com.ardaslegends.presentation.discord.utils.DiscordUtils;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.*;
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
    private final RemoveCommand remove;
    private final StockpileCommand stockpile;
    private final DeclareCommand declare;
    private final Map<String, ALCommandExecutor> executions;

    private final BotProperties properties;
    public Commands(DiscordApi api, BindCommand bind, RegisterCommand register, CreateCommand create, DeleteCommand delete, BotProperties properties,
                    UpdateCommand update, MoveCommand move, CancelCommand cancel, InjureCommand injure, HealCommand heal, UnbindCommand unbind,
                    DisbandCommand disband, InfoCommand info, StationCommand station, UnstationCommand unstation, StockpileCommand stockpile,
                    PickSiegeCommand pickSiege, RemoveCommand remove, DeclareCommand declare
    ) {
        this.api = api;
        this.bind = bind;
        this.register = register;
        this.create = create;
        this.delete = delete;
        this.properties = properties;
        this.info = info;
        this.stockpile = stockpile;
        this.remove = remove;
        this.declare = declare;

        executions = new HashMap<>();

        Set<SlashCommandBuilder> commands = new HashSet<>();
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
        commands.add(stockpile.init(executions));
        commands.add(pickSiege.init(executions));
        commands.add(remove.init(executions));
        commands.add(declare.init(executions));

        api.bulkOverwriteGlobalApplicationCommands(commands).join();
        log.info("Updated [{}] global commands", commands.size());

        log.debug("Fetching roleplay-commands channel with ID in Property file");
        TextChannel rpCommandsChannel = api.getTextChannelById(properties.getRpCommandsChannel()).orElseThrow();

        // Sets up listeners on channels that delete all messages that do not come from the bot or are not slashcommands
        setupListeners(Set.of(rpCommandsChannel));

        api.addSlashCommandCreateListener(event -> {

            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            try {

                var responseUpdater = interaction.respondLater().join();

                EmbedBuilder embed;
                ALMessageResponse response = null;
                try {
                    String fullname = getFullCommandName(interaction);
                    log.trace("Full CommandName: [{}]", fullname);
                    List<SlashCommandInteractionOption> options = getOptions(interaction);

                    log.trace("List of available options: {}", options.stream()
                            .map(interactionOption -> interactionOption.getName())
                            .collect(Collectors.joining(", ")));

                    log.info("Incoming '/{}' command", fullname);
                    log.trace("Calling command execution function");
                    response = executions.get(fullname).execute(interaction, options, properties);

                    if(response == null) {
                        responseUpdater.delete();
                        return;
                    }

                    if (response.hasMessage()) {
                        responseUpdater.delete();

                        response.message()
                                // TODO Change to war channel
                                .send(rpCommandsChannel);
                    }
                    else {
                        responseUpdater.addEmbed(response.embed()).update().join();
                    }

                    log.info("Finished handling '/{}' command", fullname);
                } catch (BotException exception) {
                    log.warn("Encountered ServiceException while executing, msg: {}", exception.getMessage());
                    embed = createErrorEmbed(exception.getTitle(), exception.getMessage());
                    responseUpdater.addEmbed(embed).update().join();
                } catch (Exception exception) {
                    log.error("ENCOUNTERED UNEXPECTED ERROR OF TYPE {} - MSG: {}", exception.getClass(), exception.getMessage());
                    exception.printStackTrace();
                    String message = exception.getMessage() + "\nPlease contact the devs!";
                    embed = createErrorEmbed("An unexpected error occured", message);
                    responseUpdater.addEmbed(embed).update().join();
                }



                log.debug("Updating response to new embed");
                // The join() is important so that the exceptions go into the catch blocks
            } catch (Exception e) {
                // TODO: Create Error Report of Stacktrace and stuff, until then we're throwing this again
                // TODO: This does not
                e.printStackTrace();
            }
        });
    }


    private void setupListeners(Set<TextChannel> channels) {
        channels.stream()
                .forEach(textChannel -> {
                    textChannel.addMessageCreateListener(messageCreateEvent -> {
                        var author = messageCreateEvent.getMessageAuthor();
                        var message = messageCreateEvent.getMessage();

                  //      if(!author.isYourself() && !message.getContent().startsWith("/")) {
                    //        messageCreateEvent.deleteMessage();
                      //  }
                    });
                });
    }

}
