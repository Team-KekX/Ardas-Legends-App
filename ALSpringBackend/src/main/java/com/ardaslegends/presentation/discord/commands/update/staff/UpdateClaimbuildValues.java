package com.ardaslegends.presentation.discord.commands.update.staff;

import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.ClaimBuildService;
import com.ardaslegends.service.dto.claimbuild.CreateClaimBuildDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor

@Slf4j
public class UpdateClaimbuildValues implements ALStaffCommandExecutor {

    private final ClaimBuildService claimBuildService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /update claimbuild request, getting option-data");

        checkStaff(interaction, properties.getStaffRoles());

        log.debug("Fetching option-data");
        String name = getStringOption("cbname", options);
        log.trace("UpdateClaimbuild: Name is [{}]", name);

        String region = getStringOption("region", options);
        log.trace("UpdateClaimbuild: Region is [{}]", region);

        String type = getStringOption("type", options);
        log.trace("UpdateClaimbuild: ClaimbuildType is [{}]", type);

        String faction = getStringOption("faction", options);
        log.trace("UpdateClaimbuild: Faction is [{}]", faction);

        int x = getLongOption("x", options).intValue();
        log.trace("UpdateClaimbuild: X-Coordinate is [{}]", x);

        int y = getLongOption("y", options).intValue();
        log.trace("UpdateClaimbuild: Y-CoordinateName is [{}]", y);

        int z = getLongOption("z", options).intValue();
        log.trace("UpdateClaimbuild: Z-Coordinate is [{}]", z);

        String traders = getStringOption("traders", options);
        log.trace("UpdateClaimbuild: Traders are [{}]", traders);

        String sieges = getStringOption("sieges", options);
        log.trace("UpdateClaimbuild: Sieges are [{}]", sieges);

        String numberOfHouses = getStringOption("number-of-houses", options);
        log.trace("UpdateClaimbuild: number of houses is [{}]", numberOfHouses);

        String builtBy = getStringOption("built-by", options);
        log.trace("UpdateClaimbuild: built by is [{}]", builtBy);

        String productionSites = getOptionalStringOption("production-sites", options).orElse("none");
        log.trace("UpdateClaimbuild: Production Sites are  [{}]", productionSites);

        String specialBuildings = getOptionalStringOption("special-buildings", options).orElse("none");
        log.trace("UpdateClaimbuild: Special Buildings are  [{}]", specialBuildings);


        log.debug("CreateClaimbuild: Building Dto");
        CreateClaimBuildDto dto = new CreateClaimBuildDto(name,region,type,faction,x,y,z,productionSites,specialBuildings,traders,sieges,numberOfHouses,builtBy);
        log.debug("Dto result [{}]", dto);

        log.debug("UpdateClaimbuild: Calling createClaimbuild Service");
        var claimbuild = discordServiceExecution(dto,false, claimBuildService::createClaimbuild, "Error during Claimbuild Creation");
        log.debug("UpdateClaimbuild: Result [{}]", claimbuild);


        String prodString = createProductionSiteString(claimbuild.getProductionSites());
        String specialBuildingsString = createSpecialBuildingsString(claimbuild.getSpecialBuildings());
        String builtByString = claimbuild.getBuiltBy().stream().map(player -> player.getIgn()).collect(Collectors.joining(", "));

        return new EmbedBuilder()
                .setTitle("Claimbuild %s was successfully updated!".formatted(claimbuild.getName()))
                .setColor(ALColor.YELLOW)
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
                .setThumbnail(getFactionBanner(claimbuild.getOwnedBy().getName()))
                .setTimestampToNow();
    }
}
