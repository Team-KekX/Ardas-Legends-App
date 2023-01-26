package com.ardaslegends.presentation.api;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.RPChar;
import com.ardaslegends.domain.Region;
import com.ardaslegends.presentation.abstraction.ControllerUnitTest;
import com.ardaslegends.presentation.api.response.player.PlayerResponse;
import com.ardaslegends.presentation.api.response.player.PlayerRpCharResponse;
import com.ardaslegends.presentation.api.response.player.PlayerUpdateDiscordIdResponse;
import com.ardaslegends.presentation.api.response.player.rpchar.RpCharResponse;
import com.ardaslegends.service.FactionService;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.dto.player.*;
import com.ardaslegends.service.dto.player.rpchar.CreateRPCharDto;
import com.ardaslegends.service.dto.player.rpchar.UpdateRpCharDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@Slf4j
public class PlayerRestControllerTest extends ControllerUnitTest {


    private PlayerService mockPlayerService;
    private FactionService mockFactionService;

    private PlayerRestController playerRestController;

    String ign = "Luktronic";
    String discordId = "1234";

    Player player;
    RPChar rpChar;
    Region region;

    PlayerResponse expectedPlayerResponse;
    RpCharResponse expectedRpCharResponse;
    PlayerRpCharResponse expectedPlayerRpCharResponse;

    Faction gondor;
    Faction mordor;

    CreatePlayerDto createPlayerDto;
    CreateRPCharDto createRPCharDto;
    DiscordIdDto discordIdDto = new DiscordIdDto(discordId);


    @BeforeEach
    void setup() {
        mockPlayerService = mock(PlayerService.class);
        mockFactionService = mock(FactionService.class);
        playerRestController = new PlayerRestController(mockPlayerService, mockFactionService);
        baseSetup(playerRestController, PlayerRestController.BASE_URL);

        gondor = Faction.builder().name("Gondor").build();
        mordor = Faction.builder().name("Mordor").build();
        region = Region.builder().id("102").build();
        rpChar = RPChar.builder().injured(true).isHealing(false).currentRegion(region).name("Belegorn").title("King of Gondor").gear("Best").pvp(true).build();
        player = Player.builder().ign(ign).faction(gondor).discordID(discordId).rpChar(rpChar).build();
        expectedPlayerResponse = new PlayerResponse(player);
        expectedRpCharResponse = new RpCharResponse(rpChar);
        expectedPlayerRpCharResponse = new PlayerRpCharResponse(player);
        createPlayerDto = new CreatePlayerDto(ign, discordId, gondor.getName());
        createRPCharDto = new CreateRPCharDto(discordId, rpChar.getName(), rpChar.getTitle(), rpChar.getGear(), rpChar.getPvp());

        when(mockPlayerService.getPlayerByIgn(ign)).thenReturn(player);
        when(mockPlayerService.getPlayerByDiscordId(discordId)).thenReturn(player);
        when(mockPlayerService.createPlayer(createPlayerDto)).thenReturn(player);
        when(mockPlayerService.createRoleplayCharacter(createRPCharDto)).thenReturn(rpChar);
        when(mockFactionService.getFactionByName(gondor.getName())).thenReturn(gondor);
        when(mockFactionService.getFactionByName(mordor.getName())).thenReturn(mordor);


    }

    // Create Method Tests

    @Test
    void ensureCreatePlayerWorksProperly() throws Exception {
        // Act
        var result = deserialize(post("", createPlayerDto).getResponse(), PlayerResponse.class);
        // Assert
        assertThat(result).isEqualTo(expectedPlayerResponse);
    }

    // ---------------------------------------------------    Create RPChar Test

    @Test
    void ensureCreateRpCharWorksProperly() throws Exception {
        var result = deserialize(post(PlayerRestController.PATH_RPCHAR, createRPCharDto).getResponse(), RpCharResponse.class);

        assertThat(result).isEqualTo(expectedRpCharResponse);
    }

    // Read Methods Test

    // by Ign
    @Test
    void ensureGetByIgnWorksProperly() throws Exception{
        // Act
        var result = deserialize(get(PlayerRestController.PATH_GET_BY_IGN.replace("{ign}", player.getIgn()), null).getResponse(), PlayerRpCharResponse.class);

        // Assert
        assertThat(result).isEqualTo(expectedPlayerRpCharResponse);
    }

    // by DiscordId

    @Test
    void ensureGetByDiscordIdWorksProperly() throws Exception{
        // Act
        var result = deserialize(get(PlayerRestController.PATH_GET_BY_DISCORD_ID.replace("{discId}", player.getDiscordID()), null).getResponse(), PlayerRpCharResponse.class);

        // Assert
        assertThat(result).isEqualTo(expectedPlayerRpCharResponse);
    }

    @Test
    void ensureUpdatePlayerFactionWorks() throws Exception {
        log.debug("Testing if update Player Faction Works...");
        // Assign
        UpdatePlayerFactionDto dto = new UpdatePlayerFactionDto(player.getDiscordID(), mordor.getName());

        player.setFaction(mordor);
        expectedPlayerResponse = new PlayerResponse(player);

        when(mockPlayerService.updatePlayerFaction(dto)).thenReturn(player);

        //Act
        var result = deserialize(patch(PlayerRestController.PATH_FACTION, dto).getResponse(), PlayerResponse.class);

        assertThat(result).isEqualTo(expectedPlayerResponse);
        log.info("Test passed: updatePlayerFaction works properly when using correct values!");
    }

    // Update Ign Tests

    @Test
    void ensureUpdateIgnWorksProperly() throws Exception {
        log.debug("Testing if update ign works properly");

        // Assign
        UpdatePlayerIgnDto dto = new UpdatePlayerIgnDto("New Ign", discordId);

        player.setIgn(dto.ign());
        expectedPlayerResponse = new PlayerResponse(player);

        when(mockPlayerService.updateIgn(dto)).thenReturn(player);

        // Act
        var result = deserialize(patch(PlayerRestController.PATH_IGN, dto).getResponse(), PlayerResponse.class);

        assertThat(result).isEqualTo(expectedPlayerResponse);
        log.info("Test passed: updateIgn works properly when using correct values!");
    }

    // Update DiscordId

    @Test
    void ensureUpdateDiscordIdWorksProperly() throws Exception {
        log.debug("Testing if update discordId works properly");
        // Assign
        UpdateDiscordIdDto dto = new UpdateDiscordIdDto(discordId, "NEW" + discordId);
        player.setDiscordID(dto.newDiscordId());
        PlayerUpdateDiscordIdResponse expectedResponse = new PlayerUpdateDiscordIdResponse(player, dto.oldDiscordId());

        when(mockPlayerService.updateDiscordId(dto)).thenReturn(player);

        // Act
        var result = deserialize(patch(PlayerRestController.PATH_DISCORDID, dto).getResponse(), PlayerUpdateDiscordIdResponse.class);

        assertThat(result).isEqualTo(expectedResponse);
        log.info("Test passed: updateDiscordId works properly when using correct values!");
    }

    // Update Character Name

    @Test
    void ensureUpdateCharacterNameWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterName works properly with correct values");

        // Assign
        UpdateRpCharDto dto = new UpdateRpCharDto(discordId, "New name", null, null, null, null, null);

        rpChar.setName(dto.charName());
        expectedRpCharResponse = new RpCharResponse(rpChar);

        when(mockPlayerService.updateCharacterName(dto)).thenReturn(rpChar);

        // Act
        var result = deserialize(patch(PlayerRestController.PATH_RPCHAR_NAME, dto).getResponse(), RpCharResponse.class);

        assertThat(result).isEqualTo(expectedRpCharResponse);
    }

    // Update title

    @Test
    void ensureUpdateCharacterTitleWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterTitle works properly with correct values");
        // Assign
        UpdateRpCharDto dto = new UpdateRpCharDto(discordId, null,"New Title", null, null, null, null);

        rpChar.setTitle(dto.title());
        expectedRpCharResponse = new RpCharResponse(rpChar);

        when(mockPlayerService.updateCharacterTitle(dto)).thenReturn(rpChar);

        // Act
        var result = deserialize(patch(PlayerRestController.PATH_RPCHAR_TITLE, dto).getResponse(), RpCharResponse.class);

        assertThat(result).isEqualTo(expectedRpCharResponse);
    }

    // Update Gear
    @Test
    void ensureUpdateCharacterGearWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterGear works properly with correct values");

        // Assign
        UpdateRpCharDto dto = new UpdateRpCharDto(discordId, null,null, null, null, "New Gear", null);

        rpChar.setGear(dto.gear());
        expectedRpCharResponse = new RpCharResponse(rpChar);

        when(mockPlayerService.updateCharacterGear(dto)).thenReturn(rpChar);

        // Act
        var result = deserialize(patch(PlayerRestController.PATH_RPCHAR_GEAR, dto).getResponse(), RpCharResponse.class);
        assertThat(result).isEqualTo(expectedRpCharResponse);
    }

    // Update PvP

    @Test
    void ensureUpdateCharacterPvPWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterPvp works properly with correct values");

        // Assign
        UpdateRpCharDto dto = new UpdateRpCharDto(discordId, null,null, null, null, null, false);

        rpChar.setPvp(dto.pvp());
        expectedRpCharResponse = new RpCharResponse(rpChar);

        when(mockPlayerService.updateCharacterPvp(dto)).thenReturn(rpChar);

        // Act
        var result = deserialize(patch(PlayerRestController.PATH_RPCHAR_PVP, dto).getResponse(), RpCharResponse.class);
        assertThat(result).isEqualTo(expectedRpCharResponse);
    }
    // ------------------------------------------- Delete Methods

    // Delete Player

    @Test
    void ensureDeletePlayerWorksProperly() throws Exception {
        log.debug("Testing if deletePlayer works properly");

        // Assign
        when(mockPlayerService.deletePlayer(discordIdDto)).thenReturn(player);

        // Act
        var result = deserialize(delete("", discordIdDto).getResponse(), PlayerResponse.class);
        assertThat(result).isEqualTo(expectedPlayerResponse);
    }

    // Delete RpChar

    @Test
    void ensureDeleteRpCharWorksProperly() throws Exception {
        log.debug("Testing if RpChar works properly");

        // Assign
        when(mockPlayerService.deleteRpChar(discordIdDto)).thenReturn(rpChar);

        // Act
        var result = deserialize(delete(PlayerRestController.PATH_RPCHAR, discordIdDto).getResponse(), RpCharResponse.class);
        assertThat(result).isEqualTo(expectedRpCharResponse);
    }


    @Test
    void ensureInjureCharWorksProperly() throws Exception {
        log.debug("Testing if injureChar works properly with correct values");

        // Assign
        rpChar.setInjured(true);
        expectedRpCharResponse = new RpCharResponse(rpChar);

        when(mockPlayerService.injureChar(discordIdDto)).thenReturn(rpChar);

        // Act
        var result = deserialize(patch(PlayerRestController.PATH_INJURE, discordIdDto).getResponse(), RpCharResponse.class);

        assertThat(result).isEqualTo(expectedRpCharResponse);
        log.info("Test passed: injure RPChar builds the correct response");
    }

    @Test
    void ensureStartHealWorksProperly() throws Exception {
        log.debug("Testing if startHeal works properly with correct values");

        // Assign
        when(mockPlayerService.healStart(discordIdDto)).thenReturn(rpChar);

        // Act
        var result = deserialize(patch(PlayerRestController.PATH_HEAL_START, discordIdDto).getResponse(), RpCharResponse.class);

        assertThat(result).isEqualTo(expectedRpCharResponse);
        log.info("Test passed: startHeal builds the correct response");
    }

    @Test
    void ensureStopHealWorksProperly() throws Exception {
        log.debug("Testing if stopHeal works properly with correct values");
        // Assign
        when(mockPlayerService.healStop(discordIdDto)).thenReturn(rpChar);

        // Act
        var result = deserialize(patch(PlayerRestController.PATH_HEAL_STOP, discordIdDto).getResponse(), RpCharResponse.class);

        assertThat(result).isEqualTo(expectedRpCharResponse);
        log.info("Test passed: stopHeal builds the correct response");
    }
}
