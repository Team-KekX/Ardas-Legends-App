package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.presentation.exceptions.BadArgumentException;
import com.ardaslegends.data.presentation.exceptions.InternalServerException;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.*;
import com.ardaslegends.data.service.dto.player.rpchar.CreateRPCharDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
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
@RequestMapping(PlayerRestController.BASE_URL)
public class PlayerRestController {

    public final static String BASE_URL = "/api/player";

    public static final String PATH_CREATE_PLAYER = "/create";
    public static final String PATH_CREATE_RPCHAR = "/create/rpchar";

    public static final String PATH_UPDATE_FACTION = "/update/faction";
    public static final String PATH_UPDATE_IGN = "/update/ign";
    public static final String PATH_UPDATE_DISCORDID = "/update/discordid";

    public static final String PATH_DELETE_PLAYER = "/delete";
    public static final String PATH_DELETE_RPCHAR = "/delete/rpchar";

    public static final String PATH_GET_BY_IGN = "/getByIgn/{ign}";
    public static final String PATH_GET_BY_DISCORD_ID = "/getByDiscId/{discId}";

    private final PlayerService playerService;
    private final FactionService factionService;

    @GetMapping(PATH_GET_BY_IGN)
    public HttpEntity<Player> getByIgn(@PathVariable String ign) {
        log.debug("Incoming getByIgn Request. Ign: {}", ign);

        Player playerFound = null;
        try {
            log.debug("Calling PlayerService.getPlayerByIgn, Ign: {}", ign);
            playerFound = playerService.getPlayerByIgn(ign);
        } catch (NullPointerException e) {
            log.warn("Exception, {}, thrown because of bad arguments", e.getClass().getSimpleName());
            throw new BadArgumentException(e.getMessage(), e);
        } catch (ServiceException e) {
            log.warn("Exception, {}, thrown while fetching player by ign. Ign {}", e.getClass().getSimpleName(), ign);
            if (e.getMessage().contains("No record of type"))
                throw new BadArgumentException(e.getMessage(), e);
            throw new InternalServerException(e.getMessage(), e);
        }

        log.info("Successfully fetched player ({}) by ign ({})", playerFound, playerFound.getIgn());
        return ResponseEntity.ok(playerFound);
    }

    @GetMapping(PATH_GET_BY_DISCORD_ID)
    public HttpEntity<Player> getByDiscordId(@PathVariable String discId) {
        log.debug("Incoming getByDiscordId Request. DiscordId: {}", discId);

        Player playerFound = null;
        try {
            log.debug("Calling PlayerService.getPlayerByDiscordId, DiscordId: {}", discId);
            playerFound = playerService.getPlayerByDiscordId(discId);
        } catch (NullPointerException e) {
            log.warn("Exception, {}, thrown because of bad arguments", e.getClass().getSimpleName());
            throw new BadArgumentException(e.getMessage(), e);
        } catch (ServiceException e) {
            log.warn("Exception, {}, thrown while fetching player by DiscordId. DiscordId {}", e.getClass().getSimpleName(), discId);
            if (e.getMessage().contains("No record of type"))
                throw new BadArgumentException(e.getMessage(), e);
            throw new InternalServerException(e.getMessage(), e);
        }

        log.info("Successfully fetched player ({}) by DiscordId ({})", playerFound, playerFound.getDiscordID());
        return ResponseEntity.ok(playerFound);
    }

    @PostMapping(PATH_CREATE_PLAYER)
    public HttpEntity<Player> createPlayer(@RequestBody CreatePlayerDto createPlayerDto) {
        log.debug("Incoming createPlayer Request. Data [{}]", createPlayerDto);

        Player createdPlayer = null;
        try {
            log.debug("Calling PlayerService.createPlayer. Data {}" ,createPlayerDto);
            createdPlayer = playerService.createPlayer(createPlayerDto);
        } catch (NullPointerException | IllegalArgumentException ex) {
            log.warn("Exception, {}, thrown while creating Player because of Bad Arguments. Data {}", ex.getClass().getSimpleName(), createPlayerDto);
            throw new BadArgumentException(ex.getMessage(), ex);
        } catch (ServiceException se) {
            log.warn("Exception, {}, thrown while creating Player because of Database Problems. Data {}", se.getClass().getSimpleName(), createPlayerDto);
            throw new InternalServerException(se.getMessage(), se);
        }


        URI self = UriComponentsBuilder.fromPath(BASE_URL + PATH_GET_BY_IGN)
                .uriVariables(Map.of("ign", createdPlayer.getIgn()))
                .build().toUri();
        log.debug("URI built. Data {}, URI {}", createPlayerDto, self.toString());

        log.info("Sending HttpResponse with successfully created Player {}", createdPlayer);
        return ResponseEntity.created(self).body(createdPlayer);
    }

    @PostMapping(PATH_CREATE_RPCHAR)
    public HttpEntity<RPChar> createRpChar(@RequestBody CreateRPCharDto createRPCharDto) {
        log.debug("Incoming createRpChar Request. Data [{}]", createRPCharDto);

        RPChar createdRpChar = null;
        try {
            log.debug("Calling PlayerService.createRoleplayCharacter. Data [{}]", createRPCharDto);
            createdRpChar = playerService.createRoleplayCharacter(createRPCharDto);
        } catch (NullPointerException | IllegalArgumentException ex) {
            log.warn("Exception, {}, thrown while creating Player because of Bad Arguments. Data {}", ex.getClass().getSimpleName(), createRPCharDto);
            throw new BadArgumentException(ex.getMessage(), ex);
        } catch (ServiceException se) {
            log.warn("Exception, {}, thrown while creating Player because of Database Problems. Data {}", se.getClass().getSimpleName(), createRPCharDto);
            throw new InternalServerException(se.getMessage(), se);
        }

        log.info("Sending HttpResponse with successfully created Player {}", createdRpChar);
        return ResponseEntity.ok(createdRpChar);
    }


    @PatchMapping(PATH_UPDATE_FACTION)
    public HttpEntity<Player> updatePlayerFaction(@RequestBody UpdatePlayerFactionDto updatePlayerFactionDto) {
        log.debug("Incoming updatePlayerFaction Request. Data {}", updatePlayerFactionDto);

        Player player = null;

        /*
        Updating the player's faction
         */
        
        try {
            log.trace("Trying to update the player's faction");
            player = playerService.updatePlayerFaction(updatePlayerFactionDto);
            log.debug("Successfully updated faction without encountering any errors");
        }
        catch (NullPointerException | IllegalArgumentException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BadArgumentException(e.getMessage(), e);
        }
        catch (ServiceException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new InternalServerException(e.getMessage(), e);
        }
        
        log.trace("Building URI for player...");
        URI self = UriComponentsBuilder.fromPath(BASE_URL + PATH_GET_BY_IGN)
                .uriVariables(Map.of("ign", player.getIgn()))
                .build().toUri();
        log.debug("URI built. Data {}, URI {}", updatePlayerFactionDto, self.toString());

        log.debug("Updating player done!");
        log.info("Sending HttpResponse with successfully updated Player {}", player);
        return ResponseEntity.ok(player);
    }

    @PatchMapping(PATH_UPDATE_IGN)
    public HttpEntity<Player> updatePlayerIgn(@RequestBody UpdatePlayerIgnDto dto) {

        log.debug("Incoming updatePlayerIgn Request: Data [{}]", dto);

        Player player = null;

        try {

            log.trace("Trying to update the player's ingame name");
            player = playerService.updateIgn(dto);
            log.debug("Successfully updated faction without encountering any errors");

        } catch (NullPointerException | IllegalArgumentException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BadArgumentException(e.getMessage(), e);
        } catch (ServiceException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new InternalServerException(e.getMessage(), e);
        }

        log.debug("Updating players ign done!");
        log.info("Sending HttpResponse with successfully updated Player {}", player);
        return ResponseEntity.ok(player);
    }

    @PatchMapping(PATH_UPDATE_DISCORDID)
    public HttpEntity<Player> updatePlayerDiscordId(@RequestBody UpdateDiscordIdDto dto) {
        log.debug("Incoming updateDiscordId Request: Data [{}]", dto);

        Player player = null;

        try {
            log.trace("Trying to update the player's discordId");
            player = playerService.updateDiscordId(dto);
            log.debug("Successfully updated discordId without encountering any errors");
        } catch (NullPointerException | IllegalArgumentException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BadArgumentException(e.getMessage(), e);
        } catch (ServiceException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new InternalServerException(e.getMessage(), e);
        }

        log.debug("Updating players discordId done!");
        log.info("Sending HttpResponse with successfully updated Player {}", player);
        return ResponseEntity.ok(player);
    }

    @DeleteMapping(PATH_DELETE_PLAYER)
    public HttpEntity<Player> deletePlayer(@RequestBody DeletePlayerOrRpCharDto dto) {

        log.debug("Incoming deletePlayer Request: Data [{}]", dto);

        Player player = null;

        try {
            log.trace("Executing playerService.deletePlayer");
            player = playerService.deletePlayer(dto);
            log.debug("Successfully deleted player, [{}]", player);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.warn("Encountered exception while deleting player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BadArgumentException(e.getMessage(), e);
        } catch (ServiceException e) {
            log.warn("Encountered exception while deleting player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new InternalServerException(e.getMessage(), e);
        }

        log.info("Sending HttpResponse with successfully deleted Player [{}]", player);
        return ResponseEntity.ok(player);
    }

    @DeleteMapping(PATH_DELETE_RPCHAR)
    public HttpEntity<RPChar> deleteRpChar(@RequestBody DeletePlayerOrRpCharDto dto) {

        log.debug("Incoming deleteRpChar Request: Data [{}]", dto);

        RPChar rpChar= null;

        try {
            log.trace("Executing playerService.deleteRpChar");
            rpChar = playerService.deleteRpChar(dto);
            log.debug("Successfully deleted rpchar, [{}]", rpChar);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.warn("Encountered exception while deleting RpChar: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BadArgumentException(e.getMessage(), e);
        } catch (ServiceException e) {
            log.warn("Encountered exception while deleting RpChar: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new InternalServerException(e.getMessage(), e);
        }

        log.info("Sending HttpResponse with successfully deleted RpChar [{}]", rpChar);
        return ResponseEntity.ok(rpChar);
    }
}
