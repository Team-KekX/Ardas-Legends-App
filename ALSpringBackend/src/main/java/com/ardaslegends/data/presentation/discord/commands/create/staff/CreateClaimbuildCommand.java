package com.ardaslegends.data.presentation.discord.commands.create.staff;

import com.ardaslegends.data.domain.ClaimBuildType;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.ALStaffCommand;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class CreateClaimbuildCommand implements ALCommandExecutor, ALStaffCommand, DiscordUtils {

    private final ClaimBuildService claimBuildService;
    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, @Autowired BotProperties properties) {
        log.debug("Incoming /create claimbuild request, getting option-data");

        User user = interaction.getUser();
        Server server = interaction.getServer().get();

        log.debug("Checking if User [{}] is staff member ", user.getName());
        checkStaff(user, server, properties.getStaffRoles());

        log.debug("User [{}] is a staff member -> allowed", user.getName());

        log.debug("Fetching option-data");

        String name = getStringOption("name", interaction);
        log.trace("CreateClaimbuild: Name is [{}]", name);
        String region = getStringOption("region", interaction);
        log.trace("CreateClaimbuild: Region is [{}]", region);
        String type = getStringOption("type", interaction);
        log.trace("CreateClaimbuild: ClaimbuildType is [{}]", type);
        String faction = getStringOption("faction", interaction);
        log.trace("CreateClaimbuild: Faction is [{}]", faction);
        int x = getRequiredOption("x", interaction::getOptionLongValueByName).intValue();
        log.trace("CreateClaimbuild: X-Coordinate is [{}]", x);
        int y = getRequiredOption("y", interaction::getOptionLongValueByName).intValue();
        log.trace("CreateClaimbuild: Y-CoordinateName is [{}]", y);
        int z = getRequiredOption("z", interaction::getOptionLongValueByName).intValue();
        log.trace("CreateClaimbuild: Z-Coordinate is [{}]", z);
        String traders = getRequiredOption("traders", interaction::getOptionStringValueByName);
        log.trace("CreateClaimbuild: Traders are [{}]", traders);
        String sieges = getRequiredOption("sieges", interaction::getOptionStringValueByName);
        log.trace("CreateClaimbuild: Sieges are [{}]", sieges);
        String numberOfHouses = getRequiredOption("number-of-houses", interaction::getOptionStringValueByName);
        log.trace("CreateClaimbuild: number of houses is [{}]", numberOfHouses);
        String builtBy = getRequiredOption("built-by", interaction::getOptionStringValueByName);
        log.trace("CreateClaimbuild: built by is [{}]", builtBy);
        String productionSites = getOptionalOption("production-sites", interaction::getOptionStringValueByName).orElse("None");
        log.trace("CreateClaimbuild: Production Sites are  [{}]", productionSites);
        String specialBuildings = getOptionalOption("special-buildings", interaction::getOptionStringValueByName).orElse("None");
        log.trace("CreateClaimbuild: Special Buildings are  [{}]", specialBuildings);

        log.debug("CreateClaimbuild: Building Dto");
        CreateClaimBuildDto dto = new CreateClaimBuildDto(name,region,type,faction,x,y,z,productionSites,specialBuildings,traders,sieges,numberOfHouses,builtBy);
        log.debug("Dto result [{}]", dto);

        log.debug("CreateClaimbuild: Calling createClaimbuild Service");
        var result = discordServiceExecution(dto,false, claimBuildService::createClaimbuild, "Error during Claimbuild Creation");
        log.debug("CreateClaimbuild: Result [{}]", result);


        return new EmbedBuilder()
                ;
    }
}
