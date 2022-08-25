package com.ardaslegends.presentation.api;

import com.ardaslegends.data.domain.Movement;
import com.ardaslegends.data.domain.Path;
import com.ardaslegends.data.presentation.api.MovementRestController;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.dto.army.MoveArmyDto;
import com.ardaslegends.data.service.dto.player.DiscordIdDto;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class MovementRestControllerTest {
    MockMvc mockMvc;

    MovementService mockMovementService;

    MovementRestController movementRestController;

    @BeforeEach
    void setup() {
        mockMovementService = mock(MovementService.class);
        movementRestController = new MovementRestController(mockMovementService);
        mockMvc = MockMvcBuilders.standaloneSetup(movementRestController).build();
    }

    @Test
    void ensureCreateRoleplayCharacterMovementWorksCorrectly() throws Exception {
        log.debug("Testing if createRoleplayCharacterMovement");

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("RandoId","12.S");

        log.trace("Initialize return movement");
        Movement movement = Movement.builder().path(Path.builder().path(List.of("91", "92")).build()).build();

        log.trace("Initializing mock methods");
        when(mockMovementService.createRpCharMovement(dto)).thenReturn(movement);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/api/movement/move-char")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void ensureCancelRoleplayCharacterMoveWorksProperly() throws Exception {
        log.debug("Testing if cancelRoleplayCharacterMovement works");

        log.trace("Initializing Dto");
        DiscordIdDto dto  = new DiscordIdDto("RandoId");

        log.trace("Initialize return movement");
        Movement movement = Movement.builder().path(Path.builder().path(List.of("91", "92")).build()).build();

        log.trace("Initializing mock methods");
        when(mockMovementService.cancelRpCharMovement(dto)).thenReturn(movement);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/movement/cancel-char-move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void ensureCreateArmyMovementWorksCorrectly() throws Exception {
        log.debug("Testing if createArmyMovement works");

        log.trace("Initializing Dto");
        MoveArmyDto dto = new MoveArmyDto("1234","Knights of Gondor", "92");

        log.trace("Initialize return movement");
        Movement movement = Movement.builder().path(Path.builder().path(List.of("91", "92")).build()).build();

        log.trace("Initializing mock methods");
        when(mockMovementService.createArmyMovement(dto)).thenReturn(movement);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("http://localhost:8080/api/movement/move-army-or-company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void ensureCancelArmyMovementWorksCorrectly() throws Exception {
        log.debug("Testing if cancelArmyMovement works");

        log.trace("Initializing Dto");
        MoveArmyDto dto = new MoveArmyDto("1234","Knights of Gondor", null);

        log.trace("Initialize return movement");
        Movement movement = Movement.builder().path(Path.builder().path(List.of("91", "92")).build()).build();

        log.trace("Initializing mock methods");
        when(mockMovementService.cancelArmyMovement(dto)).thenReturn(movement);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/movement/cancel-army-move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }
}
