package com.ardaslegends.presentation.api;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.presentation.api.FactionRestController;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.dto.UpdateFactionLeaderDto;
import com.ardaslegends.data.service.dto.faction.UpdateFactionLeaderResponseDto;
import com.ardaslegends.data.service.dto.faction.UpdateStockpileDto;
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

import java.util.Arrays;

import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class FactionRestControllerTest {
    MockMvc mockMvc;

    private FactionService mockFactionService;
    private FactionRestController factionRestController;

    private Faction faction;

    ObjectMapper mapper;
    ObjectWriter ow;
    @BeforeEach
    void setup() {
        mockFactionService = mock(FactionService.class);
        factionRestController = new FactionRestController(mockFactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(factionRestController).build();

        faction = Faction.builder().name("Gondor").foodStockpile(25).build();
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    void ensureSetFactionLeaderWorksProperly() throws Exception {
        log.debug("Testing if setFactionLeader works properly");

        Player player = Player.builder().ign("Mirak").discordID("kek").build();
        Faction faction = Faction.builder().name("Gondor").leader(player).build();

        UpdateFactionLeaderDto dto = new UpdateFactionLeaderDto(faction.getName(), player.getDiscordID());
        when(mockFactionService.setFactionLeader(dto)).thenReturn(faction);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        var result = mockMvc.perform((MockMvcRequestBuilders
                .patch("http://localhost:8080/api/faction/update/faction-leader")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)))
                .andExpect(status().isOk())
                .andReturn();

        var response = result.getResponse();
        response.setCharacterEncoding("UTF-8");

        UpdateFactionLeaderResponseDto body = mapper.readValue(response.getContentAsString(), UpdateFactionLeaderResponseDto.class);

        assertThat(body.factionName()).isEqualTo(faction.getName());
        assertThat(body.factionLeaderIgn()).isEqualTo(player.getIgn());

        log.info("Test passed: update faction-leader requests work properly");
    }

    @Test
    void ensureAddStockpileWorksProperly() throws Exception {
        log.debug("Testing if addStockpile works properly with correct values");

        UpdateStockpileDto dto = new UpdateStockpileDto("Gondor", 10);
        when(mockFactionService.addToStockpile(dto)).thenReturn(faction);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        var result = mockMvc.perform((MockMvcRequestBuilders
                .patch("http://localhost:8080/api/faction/update/stockpile/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)))
                .andExpect(status().isOk())
                .andReturn();

        var response = result.getResponse();
        response.setCharacterEncoding("UTF-8");

        UpdateStockpileDto body = mapper.readValue(response.getContentAsString(),
                UpdateStockpileDto.class);

        assertThat(body.factionName()).isEqualTo(faction.getName());
        assertThat(body.amount()).isEqualTo(faction.getFoodStockpile());

        log.info("Test passed: addStockpile creates the correct response");
    }

    @Test
    void ensureRemoveStockpileWorksProperly() throws Exception {
        log.debug("Testing if removeFromStockpile works properly with correct values");

        UpdateStockpileDto dto = new UpdateStockpileDto("Gondor", 10);
        when(mockFactionService.removeFromStockpile(dto)).thenReturn(faction);

        String requestJson = ow.writeValueAsString(dto);

        var result = mockMvc.perform((MockMvcRequestBuilders
                .patch("http://localhost:8080/api/faction/update/stockpile/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)))
                .andExpect(status().isOk())
                .andReturn();

        var response = result.getResponse();
        response.setCharacterEncoding("UTF-8");

        UpdateStockpileDto body = mapper.readValue(response.getContentAsString(),
                UpdateStockpileDto.class);

        assertThat(body.factionName()).isEqualTo(faction.getName());
        assertThat(body.amount()).isEqualTo(faction.getFoodStockpile());

        log.info("Test passed: removeFromStockpile creates the correct response");
    }
    @Test
    void ensureGetStockpileInfoWorksProperly() throws Exception {
        log.debug("Testing if getStockpileInfo works properly with correct values");

        String name = faction.getName();
        UpdateStockpileDto dto = new UpdateStockpileDto("Gondor", 10);
        when(mockFactionService.getFactionByName(name)).thenReturn(faction);


        var result = mockMvc.perform((MockMvcRequestBuilders
                .get("http://localhost:8080/api/faction/get/stockpile/info/" + name)
                .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andReturn();

        var response = result.getResponse();
        response.setCharacterEncoding("UTF-8");

        UpdateStockpileDto body = mapper.readValue(response.getContentAsString(),
                UpdateStockpileDto.class);

        assertThat(body.factionName()).isEqualTo(faction.getName());
        assertThat(body.amount()).isEqualTo(faction.getFoodStockpile());

        log.info("Test passed: getStockpileInfo creates the correct response");
    }
}
