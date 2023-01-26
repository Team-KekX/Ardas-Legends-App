package com.ardaslegends.presentation.api;

import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.RPChar;
import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.api.response.player.PlayerResponse;
import com.ardaslegends.presentation.api.response.player.PlayerRpCharResponse;
import com.ardaslegends.presentation.api.response.player.PlayerUpdateDiscordIdResponse;
import com.ardaslegends.presentation.api.response.player.rpchar.RpCharResponse;
import com.ardaslegends.repository.PlayerRepository;
import com.ardaslegends.repository.ResourceRepository;
import com.ardaslegends.service.FactionService;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.dto.player.*;
import com.ardaslegends.service.dto.player.rpchar.CreateRPCharDto;
import com.ardaslegends.service.dto.player.rpchar.UpdateRpCharDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@RestController
@Tag(name = "Player Controller", description = "All REST endpoints regarding Players")
@RequestMapping(PlayerRestController.BASE_URL)
public class PlayerRestController extends AbstractRestController {

    public final static String BASE_URL = "/api/player";

    public static final String PATH_RPCHAR = "/rpchar";

    public static final String PATH_FACTION = "/faction";
    public static final String PATH_IGN = "/ign";
    public static final String PATH_DISCORDID = "/discordid";
    public static final String PATH_RPCHAR_NAME = "/rpchar/name";
    public static final String PATH_RPCHAR_TITLE = "/rpchar/title";
    public static final String PATH_RPCHAR_GEAR = "/rpchar/gear";
    public static final String PATH_RPCHAR_PVP = "/rpchar/pvp";
    public static final String PATH_INJURE = "/rpchar/injure";
    public static final String PATH_HEAL_START = "/rpchar/heal-start";
    public static final String PATH_HEAL_STOP = "/rpchar/heal-stop";
    public static final String PATH_GET_BY_IGN =  PATH_IGN + "/{ign}";
    public static final String PATH_GET_BY_DISCORD_ID = PATH_DISCORDID + "/{discId}";

    private final PlayerService playerService;
    private final FactionService factionService;

    @Operation(summary = "Get by IGN", description = "Get a player by their minecraft IGN")
    @Parameter(name = "ign", description = "Minecraft IGN of the player", example = "Luktronic")
    @GetMapping(PATH_GET_BY_IGN)
    public HttpEntity<PlayerRpCharResponse> getByIgn(@PathVariable String ign) {
        log.debug("Incoming getByIgn Request. Ign: {}", ign);

        log.debug("Calling PlayerService.getPlayerByIgn, Ign: {}", ign);
        Player playerFound = wrappedServiceExecution(ign, playerService::getPlayerByIgn);
        var response = new PlayerRpCharResponse(playerFound);

        log.info("Successfully fetched player ({}) by ign ({})", playerFound, playerFound.getIgn());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get by Discord ID", description = "Get a player by their Discord ID")
    @GetMapping(PATH_GET_BY_DISCORD_ID)
    public HttpEntity<PlayerRpCharResponse> getByDiscordId(@PathVariable String discId) {
        log.debug("Incoming getByDiscordId Request. DiscordId: {}", discId);

        log.debug("Calling PlayerService.getPlayerByDiscordId, DiscordId: {}", discId);
        Player playerFound = wrappedServiceExecution(discId, playerService::getPlayerByDiscordId);
        var response = new PlayerRpCharResponse(playerFound);

        log.info("Successfully fetched player ({}) by DiscordId ({})", playerFound, playerFound.getDiscordID());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Creates a player", description = "Create a new player in the database.")
    @PostMapping("")
    public HttpEntity<PlayerResponse> createPlayer(@RequestBody CreatePlayerDto createPlayerDto) {
        log.debug("Incoming createPlayer Request. Data [{}]", createPlayerDto);

        log.debug("Calling PlayerService.createPlayer. Data {}" ,createPlayerDto);
        Player createdPlayer = wrappedServiceExecution(createPlayerDto, playerService::createPlayer);
        var response = new PlayerResponse(createdPlayer);

        URI self = UriComponentsBuilder.fromPath(BASE_URL + PATH_GET_BY_IGN)
                .uriVariables(Map.of("ign", response.ign()))
                .build().toUri();
        log.debug("URI built. Data {}, URI {}", response, self);

        log.info("Sending HttpResponse with successfully created Player {}", createdPlayer);
        return ResponseEntity.created(self).body(response);
    }

    @Operation(summary = "Create RpChar", description = "Create a Roleplay Character")
    @PostMapping(PATH_RPCHAR)
    public HttpEntity<RpCharResponse> createRpChar(@RequestBody CreateRPCharDto createRPCharDto) {
        log.debug("Incoming createRpChar Request. Data [{}]", createRPCharDto);

        log.debug("Calling PlayerService.createRoleplayCharacter. Data [{}]", createRPCharDto);
        RPChar createdRpChar = wrappedServiceExecution(createRPCharDto, playerService::createRoleplayCharacter);
        var response = new RpCharResponse(createdRpChar);

        log.info("Sending HttpResponse with successfully created Player {}", createdRpChar);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Update Faction", description = "Update a Player's Faction")
    @PatchMapping(PATH_FACTION)
    public HttpEntity<PlayerResponse> updatePlayerFaction(@RequestBody UpdatePlayerFactionDto updatePlayerFactionDto) {
        log.debug("Incoming updatePlayerFaction Request. Data {}", updatePlayerFactionDto);

        log.trace("Trying to update the player's faction");
        Player player = wrappedServiceExecution(updatePlayerFactionDto, playerService::updatePlayerFaction);
        log.debug("Successfully updated faction without encountering any errors");
        var response = new PlayerResponse(player);

        log.trace("Building URI for player...");
        URI self = UriComponentsBuilder.fromPath(BASE_URL + PATH_GET_BY_IGN)
                .uriVariables(Map.of("ign", response.ign()))
                .build().toUri();
        log.debug("URI built. Data {}, URI {}", updatePlayerFactionDto, self.toString());

        log.debug("Updating player done!");
        log.info("Sending HttpResponse with successfully updated Player {}", player);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Update IGN", description = "Update a Player's Minecraft IGN")
    @PatchMapping(PATH_IGN)
    public HttpEntity<PlayerResponse> updatePlayerIgn(@RequestBody UpdatePlayerIgnDto dto) {

        log.debug("Incoming updatePlayerIgn Request: Data [{}]", dto);

        log.trace("Trying to update the player's ingame name");
        Player player = wrappedServiceExecution(dto, playerService::updateIgn);
        log.debug("Successfully updated faction without encountering any errors");
        var response = new PlayerResponse(player);

        log.info("Sending HttpResponse with successfully updated Player {}", player);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Discord ID", description = "Update a Player's Discord ID")
    @PatchMapping(PATH_DISCORDID)
    public HttpEntity<PlayerUpdateDiscordIdResponse> updatePlayerDiscordId(@RequestBody UpdateDiscordIdDto dto) {
        log.debug("Incoming updateDiscordId Request: Data [{}]", dto);

        log.trace("Trying to update the player's executorDiscordId");
        Player player = wrappedServiceExecution(dto, playerService::updateDiscordId);
        log.debug("Successfully updated executorDiscordId without encountering any errors");
        var response = new PlayerUpdateDiscordIdResponse(player, dto.oldDiscordId());

        log.info("Sending HttpResponse with successfully updated Player {}", player);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update RpChar name", description = "Update the name of a Roleplay Character")
    @PatchMapping(PATH_RPCHAR_NAME)
    public HttpEntity<RpCharResponse> updateCharacterName(@RequestBody UpdateRpCharDto dto) {

        log.debug("Incoming updateCharacterName Request: Data [{}]", dto);

        log.trace("Executing playerService.updateCharacterName");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::updateCharacterName);
        log.debug("Successfully updated character name without encountering any errors");
        var response = new RpCharResponse(rpChar);

        log.info("Sending HttpResponse with successfully updated RPChar [{}]", rpChar);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update RpChar title", description = "Update the title of a Roleplay Character")
    @PatchMapping(PATH_RPCHAR_TITLE)
    public HttpEntity<RpCharResponse> updateCharacterTitle(@RequestBody UpdateRpCharDto dto) {

        log.debug("Incoming updateCharacterTitle Request: Data [{}]", dto);

        log.trace("Executing playerService.updateCharacterTitle");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::updateCharacterTitle);
        log.debug("Successfully updated character title without encountering any errors");
        var response = new RpCharResponse(rpChar);

        log.info("Sending HttpResponse with successfully updated RPChar [{}]", rpChar);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update RpChar gear", description = "Update the used gear of a Roleplay Character")
    @PatchMapping(PATH_RPCHAR_GEAR)
    public HttpEntity<RpCharResponse> updateCharacterGear(@RequestBody UpdateRpCharDto dto) {

        log.debug("Incoming updateCharacterGear Request: Data [{}]", dto);

        log.trace("Executing playerService.updateCharacterGear");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::updateCharacterGear);
        log.debug("Successfully updated character Gear without encountering any errors");
        var response = new RpCharResponse(rpChar);

        log.info("Sending HttpResponse with successfully updated RPChar [{}]", rpChar);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Update RpChar PvP", description = "Update if a Roleplay Character is participating in PvP")
    @PatchMapping(PATH_RPCHAR_PVP)
    public HttpEntity<RpCharResponse> updateCharacterPvP(@RequestBody UpdateRpCharDto dto) {

        log.debug("Incoming updateCharacterPvP Request: Data [{}]", dto);

        log.trace("Executing playerService.updateCharacterPvP");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::updateCharacterPvp);
        log.debug("Successfully updated character PvP without encountering any errors");
        var response = new RpCharResponse(rpChar);

        log.info("Sending HttpResponse with successfully updated RPChar [{}]", rpChar);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete Player", description = "Delete a Player")
    @DeleteMapping("")
    public HttpEntity<PlayerResponse> deletePlayer(@RequestBody DiscordIdDto dto) {

        log.debug("Incoming deletePlayer Request: Data [{}]", dto);

        log.trace("Executing playerService.deletePlayer");
        Player player = wrappedServiceExecution(dto, playerService::deletePlayer);
        var response = new PlayerResponse(player);
        log.debug("Successfully deleted player, [{}]", player);

        log.info("Sending HttpResponse with successfully deleted Player [{}]", player);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete RpChar", description = "Delete a Roleplay Character")
    @DeleteMapping(PATH_RPCHAR)
    public HttpEntity<RpCharResponse> deleteRpChar(@RequestBody DiscordIdDto dto) {

        log.debug("Incoming deleteRpChar Request: Data [{}]", dto);

        log.trace("Executing playerService.deleteRpChar");
        RPChar rpChar = wrappedServiceExecution(dto,playerService::deleteRpChar);
        log.debug("Successfully deleted rpchar, [{}]", rpChar);
        var response = new RpCharResponse(rpChar);

        log.info("Sending HttpResponse with successfully deleted RpChar [{}]", rpChar);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Injure RpChar", description = "Injure a roleplay character")
    @PatchMapping(PATH_INJURE)
    public HttpEntity<RpCharResponse> injureChar(@RequestBody DiscordIdDto dto) {

        log.debug("Incoming injureChar Request: Data [{}]", dto);

        log.trace("Executing playerService.injureChar");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::injureChar);
        log.debug("Successfully injured character without encountering any errors");
        var response = new RpCharResponse(rpChar);

        log.info("Sending HttpResponse with successfully injured RPChar [{}]", rpChar);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Heal start", description = "Start healing a Roleplay Character")
    @PatchMapping(PATH_HEAL_START)
    public HttpEntity<RpCharResponse> healStart(@RequestBody DiscordIdDto dto) {

        log.debug("Incoming healStart Request: Data [{}]", dto);

        log.trace("Executing playerService.healStart");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::healStart);
        log.debug("Successfully started healing of character without encountering any errors");
        var response = new RpCharResponse(rpChar);

        log.info("Sending HttpResponse with successful start of healing of RPChar [{}]", rpChar);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Heal stop", description = "Cancel healing of a Roleplay Character")
    @PatchMapping(PATH_HEAL_STOP)
    public HttpEntity<RpCharResponse> healStop(@RequestBody DiscordIdDto dto) {

        log.debug("Incoming healStop Request: Data [{}]", dto);

        log.trace("Executing playerService.healStop");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::healStop);
        var response = new RpCharResponse(rpChar);
        log.debug("Successfully started healing of character without encountering any errors");

        log.info("Sending HttpResponse with successful stop of healing of RPChar [{}]", rpChar);
        return ResponseEntity.ok(response);
    }
}
