package com.ardaslegends.presentation.api;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.presentation.api.PlayerRestController;
import com.ardaslegends.data.presentation.exceptions.BadArgumentException;
import com.ardaslegends.data.presentation.exceptions.InternalServerException;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.*;
import com.ardaslegends.data.service.dto.player.rpchar.CreateRPCharDto;
import com.ardaslegends.data.service.dto.player.rpchar.UpdateRpCharDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
public class PlayerRestControllerTest {

    MockMvc mockMvc;

    private PlayerService mockPlayerService;
    private FactionService mockFactionService;

    private PlayerRestController playerRestController;

    @BeforeEach
    void setup() {
        mockPlayerService = mock(PlayerService.class);
        mockFactionService = mock(FactionService.class);
        playerRestController = new PlayerRestController(mockPlayerService, mockFactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(playerRestController).build();
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

    @Test
    void ensureCreatePlayerThrowsBadArgumentExceptionWhenNullPointerIsThrownInService() {
        // Assign
        CreatePlayerDto dto = new CreatePlayerDto("vernon", "roche", "Mordor");
        NullPointerException thrownException = new NullPointerException("Argument must not be null");
        when(mockPlayerService.createPlayer(dto)).thenThrow(thrownException);

        // Assert
        var result = assertThrows(BadArgumentException.class, () -> playerRestController.createPlayer(dto));

        assertThat(result.getMessage()).isEqualTo(thrownException.getMessage());
        assertThat(result.getCause()).isEqualTo(thrownException);
    }

    @Test
    void ensureCreatePlayerThrowsBadArgumentExceptionWhenIllegalArgumentIsThrownInService() {
        // Assign
        CreatePlayerDto dto = new CreatePlayerDto("vernon", "roche", "Mordor");
        IllegalArgumentException thrownException = new IllegalArgumentException("Argument is not valid");
        when(mockPlayerService.createPlayer(dto)).thenThrow(thrownException);

        // Assert
        var result = assertThrows(BadArgumentException.class, () -> playerRestController.createPlayer(dto));

        assertThat(result.getMessage()).isEqualTo(thrownException.getMessage());
        assertThat(result.getCause()).isEqualTo(thrownException);
    }

    @Test
    void ensureCreatePlayerThrowsInternalServerExceptionWhenPersistenceExceptionIsThrownInService() {
        // Assign
        CreatePlayerDto dto = new CreatePlayerDto("vernon", "roche", "Mordor");

        Player player = Player.builder().ign(dto.ign()).discordID(dto.discordID()).build();

        PersistenceException pEx = new PersistenceException("Database down");
        ServiceException serviceException = ServiceException.cannotCreateEntity(player, pEx);

        when(mockPlayerService.createPlayer(dto)).thenThrow(serviceException);

        // Assert
        var result = assertThrows(InternalServerException.class, () -> playerRestController.createPlayer(dto));

        assertThat(result.getMessage()).isEqualTo(serviceException.getMessage());
        assertThat(result.getCause()).isEqualTo(serviceException);
        assertThat(result.getCause().getCause()).isEqualTo(pEx);
    }

    // ---------------------------------------------------    Create RPChar Test

    @Test
    void ensureCreateRpCharWorksProperly() throws Exception {
        // Assign
        when(mockPlayerService.createRoleplayCharacter(any())).thenReturn(new RPChar());

        CreateRPCharDto dto = new CreateRPCharDto("MiraksID", "Rando", "Rando King", "Gondolin", true);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                .post("http://localhost:8080/api/player/create/rpchar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void ensureCreateRpCharReturnsBadRequestOnNPE() throws Exception {
        // Assign
        NullPointerException nullPointerException = new NullPointerException("Whoops a field was null");
        when(mockPlayerService.createRoleplayCharacter(any())).thenThrow(nullPointerException);

        CreateRPCharDto dto = new CreateRPCharDto("MiraksID", "Rando", "Rando King", "Gondolin", true);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/api/player/create/rpchar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(BadArgumentException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).isEqualTo(nullPointerException.getMessage()));
    }

    @Test
    void ensureCreateRpCharReturnsBadRequestOnIAE() throws Exception {
        // Assign
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Whoops Illegal Argument");
        when(mockPlayerService.createRoleplayCharacter(any())).thenThrow(illegalArgumentException);

        CreateRPCharDto dto = new CreateRPCharDto("MiraksID", "Rando", "Rando King", "Gondolin", true);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/api/player/create/rpchar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(BadArgumentException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).isEqualTo(illegalArgumentException.getMessage()));
    }

    @Test
    void ensureCreateRpCharReturnsInternalServerErrorOnServiceException() throws Exception {
        PersistenceException pex = new PersistenceException("Database down");
        ServiceException serviceException = ServiceException.cannotSaveEntity(null, pex);
        when(mockPlayerService.createRoleplayCharacter(any())).thenThrow(serviceException);

        CreateRPCharDto dto = new CreateRPCharDto("MiraksID", "Rando", "Rando King", "Gondolin", true);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/api/player/create/rpchar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(InternalServerException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).isEqualTo(serviceException.getMessage()));
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

    @Test
    void ensureGetByIgnThrowsBadArgumentExceptionWhenServiceThrowsNullPointerException() {
        // Assign
        String ign = "luktronic";
        NullPointerException exception = new NullPointerException("Null");
        when(mockPlayerService.getPlayerByIgn(ign)).thenThrow(exception);

        // Assert
        var result = assertThrows(BadArgumentException.class, () -> playerRestController.getByIgn(ign));

        assertThat(result.getMessage()).isEqualTo(exception.getMessage());
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    void ensureGetByIgnThrowsBadArgumentExceptionWhenServiceThrowsNoRecordServiceException() {
        // Assign
        String ign = "luktronic";
        ServiceException serviceException = ServiceException.cannotReadEntityDueToNotExisting(Player.class.getSimpleName(), "ign", ign);
        when(mockPlayerService.getPlayerByIgn(ign)).thenThrow(serviceException);

        // Assert
        var result = assertThrows(BadArgumentException.class, () -> playerRestController.getByIgn(ign));

        assertThat(result.getMessage()).isEqualTo(serviceException.getMessage());
        assertThat(result.getCause()).isEqualTo(serviceException);
    }

    @Test
    void ensureGetByIgnThrowsInternalServerExceptionWhenServiceThrowsNoRecordServiceException() {
        // Assign
        String ign = "luktronic";
        PersistenceException persistenceException = new PersistenceException("Database down");
        ServiceException serviceException = ServiceException.cannotReadEntityDueToDatabase(null, persistenceException);
        when(mockPlayerService.getPlayerByIgn(ign)).thenThrow(serviceException);

        // Assert
        var result = assertThrows(InternalServerException.class, () -> playerRestController.getByIgn(ign));

        assertThat(result.getMessage()).isEqualTo(serviceException.getMessage());
        assertThat(result.getCause()).isEqualTo(serviceException);
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
    void ensureGetByDiscordIdThrowsBadArgumentExceptionWhenServiceThrowsNullPointerException() {
        // Assign
        String discId = "luktronic";
        NullPointerException exception = new NullPointerException("Null");
        when(mockPlayerService.getPlayerByDiscordId(discId)).thenThrow(exception);

        // Assert
        var result = assertThrows(BadArgumentException.class, () -> playerRestController.getByDiscordId(discId));

        assertThat(result.getMessage()).isEqualTo(exception.getMessage());
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    void ensureGetByDiscordIdThrowsBadArgumentExceptionWhenServiceThrowsNoRecordServiceException() {
        // Assign
        String discId = "luktronic";
        ServiceException serviceException = ServiceException.cannotReadEntityDueToNotExisting(Player.class.getSimpleName(), "discordId", discId);
        when(mockPlayerService.getPlayerByDiscordId(discId)).thenThrow(serviceException);

        // Assert
        var result = assertThrows(BadArgumentException.class, () -> playerRestController.getByDiscordId(discId));

        assertThat(result.getMessage()).isEqualTo(serviceException.getMessage());
        assertThat(result.getCause()).isEqualTo(serviceException);
    }

    @Test
    void ensureGetByDiscordIdThrowsInternalServerExceptionWhenServiceThrowsNoRecordServiceException() {
        // Assign
        String discId = "luktronic";
        PersistenceException persistenceException = new PersistenceException("Database down");
        ServiceException serviceException = ServiceException.cannotReadEntityDueToDatabase(null, persistenceException);
        when(mockPlayerService.getPlayerByDiscordId(discId)).thenThrow(serviceException);

        // Assert
        var result = assertThrows(InternalServerException.class, () -> playerRestController.getByDiscordId(discId));

        assertThat(result.getMessage()).isEqualTo(serviceException.getMessage());
        assertThat(result.getCause()).isEqualTo(serviceException);
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
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(updatePlayerFactionDto);

        //Act

        String url = "http://localhost:8080/api/player/update/faction";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.debug("Post Request returned status OK");

        log.info("Test passed: updatePlayerFaction works properly when using correct values!");
    }

    @Test
    void ensureUpdatePlayerFactionThrowsBadArgumentExceptionWhenValuesNull() throws Exception {
        log.debug("Testing if update Player Faction throws BadArgumentException when DTO Values are null...");

        // Assign

        log.trace("Initializing factions");
        Faction gondor = Faction.builder().name("Gondor").build();
        Faction mordor = Faction.builder().build();

        log.trace("Initializing Players");
        Player player1 = Player.builder().faction(gondor).build();
        Player player2 = Player.builder().ign("mirak441").faction(mordor).discordID("123456789").build();

        log.trace("Initializing UpdatePlayerDto's");
        UpdatePlayerFactionDto updatePlayerFactionDto1 = new UpdatePlayerFactionDto(player1.getDiscordID(), player1.getFaction().getName());
        UpdatePlayerFactionDto updatePlayerFactionDto2 = new UpdatePlayerFactionDto(player2.getDiscordID(), player2.getFaction().getName());

        log.trace("Initializing mocked methods");
        when(mockFactionService.getFactionByName("Gondor")).thenReturn(gondor);
        when(mockPlayerService.getPlayerByIgn(player1.getIgn())).thenReturn(player1);
        when(mockPlayerService.updatePlayerFaction(updatePlayerFactionDto1)).thenThrow(new NullPointerException("ign is null!"));
        when(mockPlayerService.updatePlayerFaction(updatePlayerFactionDto2)).thenThrow(new NullPointerException("faction is null!"));

        log.trace("Building JSON for UpdatePlayerDto's");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson1 = ow.writeValueAsString(updatePlayerFactionDto1);
        String requestJson2 = ow.writeValueAsString(updatePlayerFactionDto2);


        //Act

        String url = "http://localhost:8080/api/player/update/faction";

        // Perform request 1
        log.debug("Performing ign = null Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("ign")))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("null")));
        log.debug("Post Request successfully returned expected status BAD REQUEST");

        //Perform request 2
        log.debug("Performing faction = null Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson2))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("faction")))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("null")));
        log.debug("Post Request successfully returned expected status BAD REQUEST");

        log.info("Test passed: updatePlayerFaction throws BadArgumentException when DTO values are null!");
    }

    @Test
    void ensureUpdatePlayerFactionThrowsInternalServerExceptionWhenServiceError() throws Exception {
        log.debug("Testing if update Player Faction throws InternalServerException when Service encounters Error...");

        // Assign

        log.trace("Initializing factions");
        Faction gondor = Faction.builder().name("Gondor").build();
        Faction mordor = Faction.builder().build();

        log.trace("Initializing Players");
        Player player = Player.builder().faction(gondor).build();

        log.trace("Initializing UpdatePlayerDto's");
        UpdatePlayerFactionDto updatePlayerFactionDto = new UpdatePlayerFactionDto(player.getDiscordID(), mordor.getName());

        log.trace("Initializing mocked methods");
        when(mockFactionService.getFactionByName("Gondor")).thenReturn(gondor);
        when(mockPlayerService.getPlayerByIgn(player.getIgn())).thenReturn(player);
        when(mockPlayerService.updatePlayerFaction(updatePlayerFactionDto)).thenThrow(
                ServiceException.cannotSaveEntity(player, new PersistenceException("PersistenceException")
        ));

        log.trace("Building JSON for UpdatePlayerDto's");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson1 = ow.writeValueAsString(updatePlayerFactionDto);

        //Act

        String url = "http://localhost:8080/api/player/update/faction";

        // Perform request 1
        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson1))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InternalServerException));
        log.debug("Post Request successfully returned expected status INTERNAL SERVER ERROR");

        log.info("Test passed: updatePlayerFaction throws InternalServerException when Service encounters Error!");
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/update/ign";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                .patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

        log.debug("Patch Request returned status OK");

        log.info("Test passed: updateIgn works properly when using correct values!");
    }

    @Test
    void ensureUpdateIgnThrowsBadArgumentWhenNPEIsThrownInService() throws Exception {
        log.debug("Testing if update ign works properly");

        // Assign

        log.trace("Initializing Player Object");
        Player returnedPlayer = Player.builder().ign("Player").build();

        log.trace("Initializing UpdateIgn Data Transfer Object");
        UpdatePlayerIgnDto dto = new UpdatePlayerIgnDto("Random", "RandomId");

        log.trace("Initializing mocked method");
        NullPointerException nullPointerException = new NullPointerException("Some Null Value");
        when(mockPlayerService.updateIgn(dto)).thenThrow(nullPointerException);

        log.trace("Building JSON from UpdatePlayerIgnDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/update/ign";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        log.debug("Patch Request returned status BadRequest");

        log.info("Test passed: updateIgn returns BadRequest when NPE is thrown");
    }

    @Test
    void ensureUpdateIgnThrowsInternalServerErrorWhenServiceExceptionIsThrownInService() throws Exception {
        log.debug("Testing if update ign works properly");

        // Assign

        log.trace("Initializing Player Object");
        Player returnedPlayer = Player.builder().ign("Player").build();

        log.trace("Initializing UpdateIgn Data Transfer Object");
        UpdatePlayerIgnDto dto = new UpdatePlayerIgnDto("Random", "RandomId");

        log.trace("Initializing mocked method");
        ServiceException serviceException = ServiceException.cannotSaveEntity(null, new PersistenceException("db down"));
        when(mockPlayerService.updateIgn(dto)).thenThrow(serviceException);

        log.trace("Building JSON from UpdatePlayerIgnDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/update/ign";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError());

        log.debug("Patch Request returned status InternalServerError");

        log.info("Test passed: updateIgn returns InternalServerError when ServiceException is thrown");
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/update/discordid";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        log.debug("Patch Request returned status OK");

        log.info("Test passed: updateDiscordId works properly when using correct values!");
    }

    @Test
    void ensureUpdateDiscordIdReturnsBadRequestWhenServiceThrowsNPE() throws Exception {
        log.debug("Testing if update discordId returns BadRequest when Service throws NPE");

        // Assign

        log.trace("Initializing Nullpointer Exception");
        NullPointerException npe = new NullPointerException("Some null value");

        log.trace("Initializing UpdateDiscordId Data Transfer Object");
        UpdateDiscordIdDto dto = new UpdateDiscordIdDto("RandomOld", "RandomNew");

        log.trace("Initializing mocked method");
        when(mockPlayerService.updateDiscordId(dto)).thenThrow(npe);

        log.trace("Building JSON from UpdatePlayerDiscordIdDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/update/discordid";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getCause()).isEqualTo(npe));

        log.debug("Patch Request returned status BadRequest");

        log.info("Test passed: updateDiscordId returns BadRequest when NPE is thrown!");
    }

    @Test
    void ensureUpdateDiscordIdReturnsInternalServerErrorWhenServiceThrowsServiceException() throws Exception {
        log.debug("Testing if update discordId returns InternalServerError when Service throws ServiceException");

        // Assign

        log.trace("Initializing ServiceException");
        ServiceException serviceException = ServiceException.cannotSaveEntity(null, new PersistenceException("Database down"));

        log.trace("Initializing UpdateDiscordId Data Transfer Object");
        UpdateDiscordIdDto dto = new UpdateDiscordIdDto("RandomOld", "RandomNew");

        log.trace("Initializing mocked method");
        when(mockPlayerService.updateDiscordId(dto)).thenThrow(serviceException);

        log.trace("Building JSON from UpdatePlayerDiscordIdDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/update/discordid";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertThat(result.getResolvedException().getCause()).isEqualTo(serviceException));

        log.debug("Patch Request returned status InternalServerError");

        log.info("Test passed: updateDiscordId returns InternalServerError when ServiceException is thrown!");
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act
        String url = "http://localhost:8080/api/player/update/rpchar/name";
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act
        String url = "http://localhost:8080/api/player/update/rpchar/title";
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act
        String url = "http://localhost:8080/api/player/update/rpchar/gear";
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act
        String url = "http://localhost:8080/api/player/update/rpchar/pvp";
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
        DeletePlayerOrRpCharDto dto = new DeletePlayerOrRpCharDto("RandomId");

        log.trace("Initializing player object");
        Player player = Player.builder().discordID(dto.discordId()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.deletePlayer(dto)).thenReturn(player);

        log.trace("Building JSON from DeletePlayerOrRpCharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/delete";

        log.debug("Performing Delete request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void ensureDeletePlayerThrowsBadArgumentWhenNPEIsThrownInService() throws Exception {
        log.debug("Testing if deletePlayer returns Bad Argument Request when playerservice returns NPE");

        // Assign

        log.trace("Initializing Player Object");
        Player returnedPlayer = Player.builder().ign("Player").build();

        log.trace("Initializing DeletePlayerOrRpChar Data Transfer Object");
        DeletePlayerOrRpCharDto dto = new DeletePlayerOrRpCharDto("RandomId");

        log.trace("Initializing mocked method");
        NullPointerException nullPointerException = new NullPointerException("Some Null Value");
        when(mockPlayerService.deletePlayer(dto)).thenThrow(nullPointerException);

        log.trace("Building JSON from DeletePlayerOrRpCharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/delete";

        log.debug("Performing Delete request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        log.debug("Delete Request returned status BadRequest");

        log.info("Test passed: deletePlayer returns BadRequest when NPE is thrown");
    }

    @Test
    void ensureDeletePlayerThrowsInternalServerErrorWhenServiceExceptionIsThrownInService() throws Exception {
        log.debug("Testing if delete player returns internalServerError Request when playerservice throws ServiceException");

        // Assign

        log.trace("Initializing Player Object");
        Player returnedPlayer = Player.builder().ign("Player").build();

        log.trace("Initializing DeletePlayerOrRpChar Data Transfer Object");
        DeletePlayerOrRpCharDto dto = new DeletePlayerOrRpCharDto("RandomId");

        log.trace("Initializing mocked method");
        ServiceException serviceException = ServiceException.cannotDeleteEntity(null, new PersistenceException("db down"));
        when(mockPlayerService.deletePlayer(dto)).thenThrow(serviceException);

        log.trace("Building JSON from DeletePlayerOrRpCharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/delete";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError());

        log.debug("Delete Request returned status InternalServerError");

        log.info("Test passed: deletePlayer returns InternalServerError when ServiceException is thrown");
    }

    // Delete RpChar

    @Test
    void ensureDeleteRpCharWorksProperly() throws Exception {
        log.debug("Testing if RpChar works properly");

        // Assign

        log.trace("Initializing Dto");
        DeletePlayerOrRpCharDto dto = new DeletePlayerOrRpCharDto("RandomId");

        log.trace("Initializing rpchar object");
        RPChar rpChar = RPChar.builder().name("RandomChar").build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.deleteRpChar(dto)).thenReturn(rpChar);

        log.trace("Building JSON from DeletePlayerOrRpcharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/delete/rpchar";

        log.debug("Performing Delete request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void ensureDeleteRpCharThrowsBadArgumentWhenNPEIsThrownInService() throws Exception {
        log.debug("Testing if deleteRpChar returns Bad Argument Request when playerservice returns NPE");

        // Assign

        log.trace("Initializing DeletePlayerOrRpChar Data Transfer Object");
        DeletePlayerOrRpCharDto dto = new DeletePlayerOrRpCharDto("RandomId");

        log.trace("Initializing mocked method");
        NullPointerException nullPointerException = new NullPointerException("Some Null Value");
        when(mockPlayerService.deleteRpChar(dto)).thenThrow(nullPointerException);

        log.trace("Building JSON from DeletePlayerOrRpCharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/delete/rpchar";

        log.debug("Performing Delete request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        log.debug("Delete Request returned status BadRequest");

        log.info("Test passed: deleteRpChar returns BadRequest when NPE is thrown");
    }

    @Test
    void ensureDeleteRpCharThrowsInternalServerErrorWhenServiceExceptionIsThrownInService() throws Exception {
        log.debug("Testing if delete rpchar returns internalServerError Request when playerservice throws ServiceException");

        // Assign

        log.trace("Initializing DeletePlayerOrRpChar Data Transfer Object");
        DeletePlayerOrRpCharDto dto = new DeletePlayerOrRpCharDto("RandomId");

        log.trace("Initializing mocked method");
        ServiceException serviceException = ServiceException.cannotDeleteEntity(null, new PersistenceException("db down"));
        when(mockPlayerService.deleteRpChar(dto)).thenThrow(serviceException);

        log.trace("Building JSON from DeletePlayerOrRpCharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/delete/rpchar";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError());

        log.debug("Delete Request returned status InternalServerError");

        log.info("Test passed: deleteRpChar returns InternalServerError when ServiceException is thrown");
    }
}
