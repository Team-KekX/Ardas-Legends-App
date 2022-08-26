package com.ardaslegends.presentation.api;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.presentation.api.ArmyRestController;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.*;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ArmyRestControllerTest {

    MockMvc mockMvc;
    private ArmyService mockArmyService;
    private ArmyRestController armyRestController;

    @BeforeEach
    void setup() {
        mockArmyService = mock(ArmyService.class);
        armyRestController = new ArmyRestController(mockArmyService);
        mockMvc = MockMvcBuilders.standaloneSetup(armyRestController).build();
    }

    @Test
    void ensureCreateArmyRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController createArmy works properly with correct values");

        // Assign
        CreateArmyDto dto = new CreateArmyDto(null, null, null, null, null, null);

        when(mockArmyService.createArmy(dto)).thenReturn(new Army());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/api/army/create-army")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void ensureBindArmyRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController bindArmy works properly with correct values");

        // Assign
        BindArmyDto dto = new BindArmyDto("1234", "1234", "Knights of Gondor");

        when(mockArmyService.bind(dto)).thenReturn(new Army());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/army/bind")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: bindArmy requests get handled properly");
    }

    @Test
    void ensureUnbindArmyRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController unbindArmy works properly with correct values");

        // Assign
        BindArmyDto dto = new BindArmyDto("1234", "1234", "Knights of Gondor");

        when(mockArmyService.unbind(dto)).thenReturn(Army.builder().name("Knights of Gondor").build());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/army/unbind")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: unbindArmy requests get handled properly");
    }

    @Test
    void ensureDisbandArmyRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController disbandArmy works properly with correct values");

        // Assign
        DeleteArmyDto dto = new DeleteArmyDto("1234",  "Knights of Gondor");

        when(mockArmyService.disband(dto, false)).thenReturn(Army.builder().name("Knights of Gondor").build());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("http://localhost:8080/api/army/disband")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: disbandArmy requests get handled properly");
    }

    @Test
    void ensureDeleteArmyRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController deleteArmy works properly with correct values");

        // Assign
        DeleteArmyDto dto = new DeleteArmyDto("1234",  "Knights of Gondor");

        when(mockArmyService.disband(dto, true)).thenReturn(Army.builder().name("Knights of Gondor").build());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("http://localhost:8080/api/army/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: deleteArmy requests get handled properly");
    }

    @Test
    void ensureHealStartRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController healStart works properly with correct values");

        // Assign
        UpdateArmyDto dto = new UpdateArmyDto("kekw", "Knights of Gondor", null, null);

        when(mockArmyService.healStart(dto)).thenReturn(new Army());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/army/heal-start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: heal start requests get handled properly");
    }

    @Test
    void ensureHealStopRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController healStop works properly with correct values");

        // Assign
        UpdateArmyDto dto = new UpdateArmyDto("kekw", "Knights of Gondor", null, null);

        when(mockArmyService.healStop(dto)).thenReturn(new Army());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/army/heal-stop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: heal stop requests get handled properly");
    }

    @Test
    void ensureStationRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController station works properly with correct values");

        // Assign
        StationDto dto = new StationDto("Kek", "kek", "kek");

        when(mockArmyService.station(dto)).thenReturn(new Army());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/army/station")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: station requests get handled properly");
    }
    @Test
    void ensureUnstationRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController unstation works properly with correct values");

        // Assign
        StationDto dto = new StationDto("Kek", "kek", "kek");

        when(mockArmyService.unstation(dto)).thenReturn(new Army());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/army/unstation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: unstation requests get handled properly");
    }
    @Test
    void ensureSetFreeArmyTokensRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController setFreeArmyTokens works properly with correct values");

        // Assign
        UpdateArmyDto dto = new UpdateArmyDto(null, "Knights of Gondor", 20.0, null);

        when(mockArmyService.setFreeArmyTokens(dto)).thenReturn(Army.builder().name("Knights of Gondor").build());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/army/set-free-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: setFreeArmyTokens requests get handled properly");
    }

    @Test
    void ensurePickSiegeRequestWorksProperly() throws Exception {
        log.debug("Testing if ArmyRestController pickSiege works properly with correct values");

        // Assign
        PickSiegeDto dto = new PickSiegeDto("1234", "Knights of Gondor", "Gondor CB", "Trebuchet");

        when(mockArmyService.pickSiege(dto)).thenReturn(Army.builder().name("Knights of Gondor").build());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/army/pick-siege")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: pickSiege requests get handled properly");
    }

    @Test
    void ensureUpkeepWorksProperly() throws Exception {
        log.debug("Testing if ArmyRestController upkeep works properly");

        when(mockArmyService.upkeep()).thenReturn(List.of());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/api/army/upkeep"))
                .andExpect(status().isOk());
        log.info("Test passed: upkeep requests get handled properly");
    }

    @Test
    void ensureUpkeepPerFactionWorksProperly() throws Exception {
        log.debug("Testing if ArmyRestController upkeepPerFaction works properly");

        when(mockArmyService.getUpkeepOfFaction("Gondor")).thenReturn(new UpkeepDto(null, 0, 0));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/api/army/upkeep/Gondor"))
                .andExpect(status().isOk());
        log.info("Test passed: upkeepPerFaction requests get handled properly");
    }

    @Test
    void ensureSetPaidWorksProperly() throws Exception {
        UpdateArmyDto dto = new UpdateArmyDto(null, "kek", null, true);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/army/setPaid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: setPaid requests get handled properly");
    }
    @Test
    void ensureGetUnpaidWorksProperly() throws Exception {
        log.debug("Testing if ArmyRestController getUnpaid works properly");

        List<Army> army = List.of(Army.builder().name("kek").build());

        when(mockArmyService.getUnpaid()).thenReturn(army);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/api/army/unpaid"))
                .andExpect(status().isOk());
        log.info("Test passed: getUnpaid requests get handled properly");
    }
}
