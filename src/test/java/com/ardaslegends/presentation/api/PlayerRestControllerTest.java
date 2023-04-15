package com.ardaslegends.presentation.api;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.RPChar;
import com.ardaslegends.domain.Region;
import com.ardaslegends.presentation.abstraction.AbstractIntegrationTest;
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
import com.ardaslegends.util.TestDataFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@Slf4j
public class PlayerRestControllerTest extends AbstractIntegrationTest {

    @Autowired
    PlayerRestController playerRestController;

    Player player;
    Player player2;
    RPChar rpChar;
    Faction gondor;
    Faction mordor;

    PlayerResponse expectedPlayerResponse;
    RpCharResponse expectedRpCharResponse;
    PlayerRpCharResponse expectedPlayerRpCharResponse;
    CreatePlayerDto createPlayerDto;
    CreateRPCharDto createRPCharDto;
    DiscordIdDto discordIdDto;


    @BeforeEach
    void setup() {
        baseSetup(playerRestController, PlayerRestController.BASE_URL);

        player = TestDataFactory.playerLuktronic();
        rpChar = TestDataFactory.rpcharBelegorn(player);
        gondor = TestDataFactory.factionGondor(player);
        mordor = TestDataFactory.factionMordor(null);
        player2 = TestDataFactory.playerMirak(gondor);

        expectedPlayerResponse = new PlayerResponse(player);
        expectedRpCharResponse = new RpCharResponse(rpChar);
        expectedPlayerRpCharResponse = new PlayerRpCharResponse(player, false);
        createPlayerDto = new CreatePlayerDto(player2.getIgn(), player2.getDiscordID(), gondor.getName());
        createRPCharDto = new CreateRPCharDto(player.getDiscordID(), rpChar.getName(), rpChar.getTitle(), rpChar.getGear(), rpChar.getPvp());
        discordIdDto = new DiscordIdDto(player.getDiscordID());

    }

    // Create Method Tests

    @Test
    @Transactional
    void ensureCreatePlayerWorksProperly() throws Exception {
        // Act
        var result = post("", createPlayerDto, PlayerResponse.class);
        // Assert
        assertThat(result.getBody()).isEqualTo(expectedPlayerResponse);
    }

    // ---------------------------------------------------    Create RPChar Test

    @Test
    void ensureCreateRpCharWorksProperly() throws Exception {
        var result = post(PlayerRestController.PATH_RPCHAR, createRPCharDto, RpCharResponse.class);

        assertThat(result.getBody()).isEqualTo(expectedRpCharResponse);
    }

    // Read Methods Test

    // by Ign
    @Test
    void ensureGetByIgnWorksProperly() throws Exception{
        // Act
        var result = get(PlayerRestController.PATH_GET_BY_IGN.replace("{ign}", player.getIgn()), null, PlayerRpCharResponse.class);

        // Assert
        assertThat(result.getBody()).isEqualTo(expectedPlayerRpCharResponse);
    }

    // by DiscordId

    @Test
    void ensureGetByDiscordIdWorksProperly() throws Exception{
        // Act
        var result = get(PlayerRestController.PATH_GET_BY_DISCORD_ID.replace("{discId}", player.getDiscordID()), null, PlayerRpCharResponse.class);

        // Assert
        assertThat(result.getBody()).isEqualTo(expectedPlayerRpCharResponse);
    }

    @Test
    void ensureUpdatePlayerFactionWorks() throws Exception {
        log.debug("Testing if update Player Faction Works...");
        // Assign
        UpdatePlayerFactionDto dto = new UpdatePlayerFactionDto(player.getDiscordID(), mordor.getName());

        player.setFaction(mordor);
        expectedPlayerResponse = new PlayerResponse(player);

        //Act
        var result = patch(PlayerRestController.PATH_FACTION, dto, PlayerResponse.class);

        assertThat(result.getBody()).isEqualTo(expectedPlayerResponse);
        log.info("Test passed: updatePlayerFaction works properly when using correct values!");
    }

    // Update Ign Tests

    @Test
    void ensureUpdateIgnWorksProperly() throws Exception {
        log.debug("Testing if update ign works properly");

        // Assign
        UpdatePlayerIgnDto dto = new UpdatePlayerIgnDto("New Ign", player.getDiscordID());

        player.setIgn(dto.ign());
        expectedPlayerResponse = new PlayerResponse(player);

        // Act
        var result = patch(PlayerRestController.PATH_IGN, dto, PlayerResponse.class);

        assertThat(result.getBody()).isEqualTo(expectedPlayerResponse);
        log.info("Test passed: updateIgn works properly when using correct values!");
    }

    // Update DiscordId

    @Test
    void ensureUpdateDiscordIdWorksProperly() throws Exception {
        log.debug("Testing if update discordId works properly");
        // Assign
        UpdateDiscordIdDto dto = new UpdateDiscordIdDto(player.getDiscordID(), "NEW" + player.getDiscordID());
        player.setDiscordID(dto.newDiscordId());
        PlayerUpdateDiscordIdResponse expectedResponse = new PlayerUpdateDiscordIdResponse(player, dto.oldDiscordId());

        // Act
        var result = patch(PlayerRestController.PATH_DISCORDID, dto, PlayerUpdateDiscordIdResponse.class);

        assertThat(result.getBody()).isEqualTo(expectedResponse);
        log.info("Test passed: updateDiscordId works properly when using correct values!");
    }

    // Update Character Name

    @Test
    void ensureUpdateCharacterNameWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterName works properly with correct values");

        // Assign
        UpdateRpCharDto dto = new UpdateRpCharDto(player.getDiscordID(), "New name", null, null, null, null, null);

        rpChar.setName(dto.charName());
        expectedRpCharResponse = new RpCharResponse(rpChar);

        // Act
        var result = patch(PlayerRestController.PATH_RPCHAR_NAME, dto, RpCharResponse.class);

        assertThat(result.getBody()).isEqualTo(expectedRpCharResponse);
    }

    // Update title

    @Test
    void ensureUpdateCharacterTitleWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterTitle works properly with correct values");
        // Assign
        UpdateRpCharDto dto = new UpdateRpCharDto(player.getDiscordID(), null,"New Title", null, null, null, null);

        rpChar.setTitle(dto.title());
        expectedRpCharResponse = new RpCharResponse(rpChar);

        // Act
        var result = patch(PlayerRestController.PATH_RPCHAR_TITLE, dto, RpCharResponse.class);

        assertThat(result.getBody()).isEqualTo(expectedRpCharResponse);
    }

    // Update Gear
    @Test
    void ensureUpdateCharacterGearWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterGear works properly with correct values");

        // Assign
        UpdateRpCharDto dto = new UpdateRpCharDto(player.getDiscordID(), null,null, null, null, "New Gear", null);

        rpChar.setGear(dto.gear());
        expectedRpCharResponse = new RpCharResponse(rpChar);

        // Act
        var result = patch(PlayerRestController.PATH_RPCHAR_GEAR, dto, RpCharResponse.class);
        assertThat(result.getBody()).isEqualTo(expectedRpCharResponse);
    }

    // Update PvP

    @Test
    void ensureUpdateCharacterPvPWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterPvp works properly with correct values");

        // Assign
        UpdateRpCharDto dto = new UpdateRpCharDto(player.getDiscordID(), null,null, null, null, null, false);

        rpChar.setPvp(dto.pvp());
        expectedRpCharResponse = new RpCharResponse(rpChar);

        // Act
        var result = patch(PlayerRestController.PATH_RPCHAR_PVP, dto, RpCharResponse.class);
        assertThat(result.getBody()).isEqualTo(expectedRpCharResponse);
    }
    // ------------------------------------------- Delete Methods

    // Delete Player

    @Test
    void ensureDeletePlayerWorksProperly() throws Exception {
        log.debug("Testing if deletePlayer works properly");

        // Act
        var result = delete("", discordIdDto, PlayerResponse.class);
        assertThat(result.getBody()).isEqualTo(expectedPlayerResponse);
    }

    // Delete RpChar

    @Test
    void ensureDeleteRpCharWorksProperly() throws Exception {
        log.debug("Testing if RpChar works properly");

        // Act
        var result = delete(PlayerRestController.PATH_RPCHAR, discordIdDto, RpCharResponse.class);
        assertThat(result.getBody()).isEqualTo(expectedRpCharResponse);
    }


    @Test
    void ensureInjureCharWorksProperly() throws Exception {
        log.debug("Testing if injureChar works properly with correct values");

        // Assign
        rpChar.setInjured(true);
        expectedRpCharResponse = new RpCharResponse(rpChar);

        // Act
        var result = patch(PlayerRestController.PATH_INJURE, discordIdDto, RpCharResponse.class);

        assertThat(result.getBody()).isEqualTo(expectedRpCharResponse);
        log.info("Test passed: injure RPChar builds the correct response");
    }

    @Test
    void ensureStartHealWorksProperly() throws Exception {
        log.debug("Testing if startHeal works properly with correct values");
        // Act
        var result = patch(PlayerRestController.PATH_HEAL_START, discordIdDto, RpCharResponse.class);

        assertThat(result.getBody()).isEqualTo(expectedRpCharResponse);
        log.info("Test passed: startHeal builds the correct response");
    }

    @Test
    void ensureStopHealWorksProperly() throws Exception {
        log.debug("Testing if stopHeal works properly with correct values");

        // Act
        var result = patch(PlayerRestController.PATH_HEAL_STOP, discordIdDto, RpCharResponse.class);

        assertThat(result.getBody()).isEqualTo(expectedRpCharResponse);
        log.info("Test passed: stopHeal builds the correct response");
    }
}
