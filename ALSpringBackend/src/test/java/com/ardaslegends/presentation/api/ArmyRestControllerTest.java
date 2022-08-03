package com.ardaslegends.presentation.api;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.presentation.api.ArmyRestController;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.BindArmyDto;
import com.ardaslegends.data.service.dto.army.CreateArmyDto;
import com.ardaslegends.data.service.dto.army.DeleteArmyDto;
import com.ardaslegends.data.service.dto.army.UpdateArmyDto;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        CreateArmyDto dto = new CreateArmyDto(null, null, null, null, null);

        when(mockArmyService.createArmy(dto)).thenReturn(new Army());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/api/army/create")
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
                        .patch("http://localhost:8080/api/army/bind-army")
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
                        .patch("http://localhost:8080/api/army/unbind-army")
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
                        .delete("http://localhost:8080/api/army/disband-army")
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
                        .delete("http://localhost:8080/api/army/delete-army")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.info("Test passed: deleteArmy requests get handled properly");
    }

    @Test
    void ensureSetFreeArmyTokensRequestWorksProperly() throws Exception{
        log.debug("Testing if ArmyRestController setFreeArmyTokens works properly with correct values");

        // Assign
        UpdateArmyDto dto = new UpdateArmyDto(null, "Knights of Gondor", 20);

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
}
