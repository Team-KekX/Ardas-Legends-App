package com.ardaslegends.data.presentation.discord.commands.create.staff;

import com.ardaslegends.data.domain.ClaimBuildType;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.ALStaffCommand;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.dto.claimbuild.CreateClaimBuildDto;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor

@Slf4j
public class CreateClaimbuildCommand implements ALCommandExecutor, ALStaffCommand, DiscordUtils {

    private final ClaimBuildService claimBuildService;
    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /create claimbuild request, getting option-data");

        User user = interaction.getUser();
        Server server = interaction.getServer().get();
        var foundSubcommand = interaction.getOptionByName("claimbuild");

        if(foundSubcommand.isEmpty()) {
            throw new Error("Slashcommand subcommand does not exist");
        }

        var subcommand = foundSubcommand.get();
        var cbName = subcommand.getOptionStringValueByName("cbname");

        if(cbName.isEmpty()) {
            throw new RuntimeException("Empty cbName");
        }

        log.debug("cbname: " + cbName.get());

        log.debug("Checking if User [{}] is staff member ", user.getName());
        checkStaff(user, server, properties.getStaffRoles());

        log.debug("User [{}] is a staff member -> allowed", user.getName());

        log.debug("Fetching option-data");

        String name = getStringOption("cbname", options);
        log.trace("CreateClaimbuild: Name is [{}]", name);

        String region = getStringOption("region", options);
        log.trace("CreateClaimbuild: Region is [{}]", region);

        String type = getStringOption("type", options);
        log.trace("CreateClaimbuild: ClaimbuildType is [{}]", type);

        String faction = getStringOption("faction", options);
        log.trace("CreateClaimbuild: Faction is [{}]", faction);

        int x = getLongOption("x", options).intValue();
        log.trace("CreateClaimbuild: X-Coordinate is [{}]", x);

        int y = getLongOption("y", options).intValue();
        log.trace("CreateClaimbuild: Y-CoordinateName is [{}]", y);

        int z = getLongOption("z", options).intValue();
        log.trace("CreateClaimbuild: Z-Coordinate is [{}]", z);

        String traders = getStringOption("traders", options);
        log.trace("CreateClaimbuild: Traders are [{}]", traders);

        String sieges = getStringOption("sieges", options);
        log.trace("CreateClaimbuild: Sieges are [{}]", sieges);

        String numberOfHouses = getStringOption("number-of-houses", options);
        log.trace("CreateClaimbuild: number of houses is [{}]", numberOfHouses);

        String builtBy = getStringOption("built-by", options);
        log.trace("CreateClaimbuild: built by is [{}]", builtBy);

        String productionSites = getOptionalStringOption("production-sites", options).orElse("none");
        log.trace("CreateClaimbuild: Production Sites are  [{}]", productionSites);

        String specialBuildings = getOptionalStringOption("special-buildings", options).orElse("none");
        log.trace("CreateClaimbuild: Special Buildings are  [{}]", specialBuildings);


        log.debug("CreateClaimbuild: Building Dto");
        CreateClaimBuildDto dto = new CreateClaimBuildDto(name,region,type,faction,x,y,z,productionSites,specialBuildings,traders,sieges,numberOfHouses,builtBy);
        log.debug("Dto result [{}]", dto);

        log.debug("CreateClaimbuild: Calling createClaimbuild Service");
        var claimbuild = discordServiceExecution(dto,true, claimBuildService::createClaimbuild, "Error during Claimbuild Creation");
        log.debug("CreateClaimbuild: Result [{}]", claimbuild);


        String prodString = createProductionSiteString(claimbuild.getProductionSites());
        String specialBuildingsString = createSpecialBuildingsString(claimbuild.getSpecialBuildings());
        String builtByString = claimbuild.getBuiltBy().stream().map(player -> player.getIgn()).collect(Collectors.joining(", "));

        return new EmbedBuilder()
                .setTitle("Claimbuild %s was successfully created!".formatted(claimbuild.getName()))
                .setColor(ALColor.GREEN)
                .addInlineField("Name", claimbuild.getName())
                .addInlineField("Faction", claimbuild.getOwnedBy().getName())
                .addInlineField("Region", claimbuild.getRegion().getId())
                .addInlineField("Type", claimbuild.getType().getName())
                .addField("Production Sites", prodString)
                .addField("Special Buildings", specialBuildingsString)
                .addInlineField("Traders", claimbuild.getTraders())
                .addInlineField("Siege", claimbuild.getSiege())
                .addInlineField("Houses", claimbuild.getNumberOfHouses())
                .addInlineField("Coordinates", claimbuild.getCoordinates().toString())
                .addInlineField("Built by", builtByString)
                .setTimestampToNow();
    }
}
