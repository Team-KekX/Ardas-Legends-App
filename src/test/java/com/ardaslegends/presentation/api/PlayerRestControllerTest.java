package com.ardaslegends.presentation.api;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.RPChar;
import com.ardaslegends.service.FactionService;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.dto.player.*;
import com.ardaslegends.service.dto.player.rpchar.CreateRPCharDto;
import com.ardaslegends.service.dto.player.rpchar.UpdateRpCharDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
public class PlayerRestControllerTest {

    MockMvc mockMvc;

    private PlayerService mockPlayerService;
    private FactionService mockFactionService;

    private PlayerRestController playerRestController;
    private ObjectMapper mapper;
    private ObjectWriter ow;
    private String url;


    @BeforeEach
    void setup() {
        mockPlayerService = mock(PlayerService.class);
        mockFactionService = mock(FactionService.class);
        playerRestController = new PlayerRestController(mockPlayerService, mockFactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(playerRestController).build();
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = mapper.writer().withDefaultPrettyPrinter();
        url = "http://localhost:8080" + PlayerRestController.BASE_URL;
    }

    // Create Method Tests

    @Test
    void ensureCreatePlayerWorksProperly() {
        // Assign
        CreatePlayerDto dto = new CreatePlayerDto("vernon", "roche", "Mordor");
        Player returnedPlayer =  Player.builder().ign(dto.ign()).discordID(dto.discordID()).build();
        when(mockPlayerService.createPlayer(dto)).thenReturn(returnedPlayer);

        // Act
        var result = playerRestController.createPlayer(dto);

        // Assert
        assertThat(result.getBody()).isEqualTo(returnedPlayer);
    }

    // ---------------------------------------------------    Create RPChar Test

    @Test
    void ensureCreateRpCharWorksProperly() throws Exception {
        // Assign
        when(mockPlayerService.createRoleplayCharacter(any())).thenReturn(new RPChar());

        CreateRPCharDto dto = new CreateRPCharDto("MiraksID", "Rando", "Rando King", "Gondolin", true);
        url += PlayerRestController.PATH_RPCHAR;

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // Read Methods Test

    // by Ign
    @Test
    void ensureGetByIgnWorksProperly() {
        // Assign
        String ign = "luktronic";
        Player p = Player.builder().ign(ign).build();
        when(mockPlayerService.getPlayerByIgn(ign)).thenReturn(p);

        // Act
        var result = playerRestController.getByIgn(ign);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBody()).isEqualTo(p);
    }

    // by DiscordId

    @Test
    void ensureGetByDiscordIdWorksProperly() {
        // Assign
        String discId = "luktronic";
        Player p = Player.builder().discordID(discId).build();
        when(mockPlayerService.getPlayerByDiscordId(discId)).thenReturn(p);

        // Act
        var result = playerRestController.getByDiscordId(discId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBody()).isEqualTo(p);
    }

    @Test
    void ensureUpdatePlayerFactionWorks() throws Exception {
        log.debug("Testing if update Player Faction Works...");

        // Assign

        log.trace("Initializing factions");
        Faction gondor = Faction.builder().name("Gondor").build();
        Faction mordor = Faction.builder().name("Mordor").build();

        log.trace("Initializing Player");
        Player player = Player.builder().ign("mirak441").discordID("123456789").faction(gondor).build();
        Player updatedPlayer = Player.builder().ign("mirak441").discordID("123456789").faction(mordor).build();

        log.trace("Initializing UpdatePlayerDto");
        UpdatePlayerFactionDto updatePlayerFactionDto = new UpdatePlayerFactionDto(player.getDiscordID(), mordor.getName());

        log.trace("Initializing mocked methods");
        when(mockFactionService.getFactionByName("Gondor")).thenReturn(gondor);
        when(mockFactionService.getFactionByName("Mordor")).thenReturn(mordor);
        when(mockPlayerService.getPlayerByIgn(player.getIgn())).thenReturn(player);
        when(mockPlayerService.updatePlayerFaction(updatePlayerFactionDto)).thenReturn(updatedPlayer);

        log.trace("Building JSON for UpdatePlayerDto");
        String requestJson = ow.writeValueAsString(updatePlayerFactionDto);

        //Act

        url += PlayerRestController.PATH_FACTION;

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.debug("Post Request returned status OK");

        log.info("Test passed: updatePlayerFaction works properly when using correct values!");
    }

    // Update Ign Tests

    @Test
    void ensureUpdateIgnWorksProperly() throws Exception {
        log.debug("Testing if update ign works properly");

        // Assign

        log.trace("Initializing Player Object");
        Player returnedPlayer = Player.builder().ign("Player").build();

        log.trace("Initializing UpdateIgn Data Transfer Object");
        UpdatePlayerIgnDto dto = new UpdatePlayerIgnDto("Random", "RandomId");

        log.trace("Initializing mocked method");
        when(mockPlayerService.updateIgn(dto)).thenReturn(returnedPlayer);

        log.trace("Building JSON from UpdatePlayerIgnDto");

        String requestJson = ow.writeValueAsString(dto);

        // Act

        url += PlayerRestController.PATH_IGN;

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                .patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

        log.debug("Patch Request returned status OK");

        log.info("Test passed: updateIgn works properly when using correct values!");
    }

    // Update DiscordId

    @Test
    void ensureUpdateDiscordIdWorksProperly() throws Exception {
        log.debug("Testing if update discordId works properly");

        // Assign

        log.trace("Initializing Player Object");
        Player returnedPlayer = Player.builder().ign("Player").build();

        log.trace("Initializing UpdateDiscordId Data Transfer Object");
        UpdateDiscordIdDto dto = new UpdateDiscordIdDto("RandomOld", "RandomNew");

        log.trace("Initializing mocked method");
        when(mockPlayerService.updateDiscordId(dto)).thenReturn(returnedPlayer);

        log.trace("Building JSON from UpdatePlayerDiscordIdDto");

        String requestJson = ow.writeValueAsString(dto);

        // Act

        url += PlayerRestController.PATH_DISCORDID;

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        log.debug("Patch Request returned status OK");

        log.info("Test passed: updateDiscordId works properly when using correct values!");
    }

    // Update Character Name

    @Test
    void ensureUpdateCharacterNameWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterName works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        UpdateRpCharDto dto = new UpdateRpCharDto("rando", "wee", null, null, null, null, null);

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().name(dto.charName()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.updateCharacterName(dto)).thenReturn(rpChar);

        log.trace("Building JSON from UpdateRpCharDto");

        String requestJson = ow.writeValueAsString(dto);
        url += PlayerRestController.PATH_RPCHAR_NAME;

        // Act
        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                .patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }

    // Update title

    @Test
    void ensureUpdateCharacterTitleWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterTitle works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        UpdateRpCharDto dto = new UpdateRpCharDto("rando", null,"SauronKing", null, null, null, null);

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().name(dto.charName()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.updateCharacterTitle(dto)).thenReturn(rpChar);

        log.trace("Building JSON from UpdateRpCharDto");

        String requestJson = ow.writeValueAsString(dto);
        url += PlayerRestController.PATH_RPCHAR_TITLE;

        // Act
        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // Update Gear
    @Test
    void ensureUpdateCharacterGearWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterGear works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        UpdateRpCharDto dto = new UpdateRpCharDto("rando", null,"SauronKing", null, null, null, null);

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().name(dto.charName()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.updateCharacterGear(dto)).thenReturn(rpChar);

        log.trace("Building JSON from UpdateRpCharDto");

        String requestJson = ow.writeValueAsString(dto);
        url += PlayerRestController.PATH_RPCHAR_GEAR;

        // Act
        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // Update PvP

    @Test
    void ensureUpdateCharacterPvPWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterPvp works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        UpdateRpCharDto dto = new UpdateRpCharDto("rando", null,"SauronKing", null, null, null, null);

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().name(dto.charName()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.updateCharacterGear(dto)).thenReturn(rpChar);

        log.trace("Building JSON from UpdateRpCharDto");

        String requestJson = ow.writeValueAsString(dto);
        url += PlayerRestController.PATH_RPCHAR_PVP;

        // Act
        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }
    // ------------------------------------------- Delete Methods

    // Delete Player

    @Test
    void ensureDeletePlayerWorksProperly() throws Exception {
        log.debug("Testing if deletePlayer works properly");

        // Assign

        log.trace("Initializing Dto");
        DiscordIdDto dto = new DiscordIdDto("RandomId");

        log.trace("Initializing player object");
        Player player = Player.builder().discordID(dto.discordId()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.deletePlayer(dto)).thenReturn(player);

        log.trace("Building JSON from DiscordIdDto");

        String requestJson = ow.writeValueAsString(dto);

        // Act
        log.debug("Performing Delete request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // Delete RpChar

    @Test
    void ensureDeleteRpCharWorksProperly() throws Exception {
        log.debug("Testing if RpChar works properly");

        // Assign

        log.trace("Initializing Dto");
        DiscordIdDto dto = new DiscordIdDto("RandomId");

        log.trace("Initializing rpchar object");
        RPChar rpChar = RPChar.builder().name("RandomChar").build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.deleteRpChar(dto)).thenReturn(rpChar);

        log.trace("Building JSON from DeletePlayerOrRpcharDto");

        String requestJson = ow.writeValueAsString(dto);
        url += PlayerRestController.PATH_RPCHAR;

        // Act

        log.debug("Performing Delete request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }


    @Test
    void ensureInjureCharWorksProperly() throws Exception {
        log.debug("Testing if injureChar works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        DiscordIdDto dto = new DiscordIdDto("1234");

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().injured(true).name("Belegorn").build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.injureChar(dto)).thenReturn(rpChar);

        log.trace("Building JSON from DiscordIdDto");

        String requestJson = ow.writeValueAsString(dto);
        url += PlayerRestController.PATH_INJURE;

        // Act
        log.debug("Performing Patch request to {}", url);
        var result = mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        var request = result.getResponse();
        request.setCharacterEncoding("UTF-8");

        RPChar response = mapper.readValue(request.getContentAsString()
                ,RPChar.class);

        assertThat(response.getName()).isEqualTo(rpChar.getName());
        assertThat(response.getInjured()).isEqualTo(rpChar.getInjured());
        log.info("Test passed: delete Claimbuild builds the correct response");
    }

    @Test
    void ensureStartHealWorksProperly() throws Exception {
        log.debug("Testing if startHeal works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        DiscordIdDto dto = new DiscordIdDto("1234");

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().injured(true).isHealing(false).name("Belegorn").build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.healStart(dto)).thenReturn(rpChar);

        log.trace("Building JSON from DiscordIdDto");

        String requestJson = ow.writeValueAsString(dto);
        url += PlayerRestController.PATH_HEAL_START;

        // Act
        log.debug("Performing Patch request to {}", url);
        var result = mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        var request = result.getResponse();
        request.setCharacterEncoding("UTF-8");

        RPChar response = mapper.readValue(request.getContentAsString()
                ,RPChar.class);

        assertThat(response.getName()).isEqualTo(rpChar.getName());
        assertThat(response.getIsHealing()).isEqualTo(rpChar.getIsHealing());
        log.info("Test passed: startHeal builds the correct response");
    }

    @Test
    void ensureStopHealWorksProperly() throws Exception {
        log.debug("Testing if stopHeal works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        DiscordIdDto dto = new DiscordIdDto("1234");

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().injured(true).isHealing(false).name("Belegorn").build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.healStop(dto)).thenReturn(rpChar);

        log.trace("Building JSON from DiscordIdDto");

        String requestJson = ow.writeValueAsString(dto);
        url += PlayerRestController.PATH_HEAL_STOP;

        // Act
        log.debug("Performing Patch request to {}", url);
        var result = mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        var request = result.getResponse();
        request.setCharacterEncoding("UTF-8");

        RPChar response = mapper.readValue(request.getContentAsString()
                ,RPChar.class);

        assertThat(response.getName()).isEqualTo(rpChar.getName());
        assertThat(response.getIsHealing()).isEqualTo(rpChar.getIsHealing());
        log.info("Test passed: stopHeal builds the correct response");
    }
}
