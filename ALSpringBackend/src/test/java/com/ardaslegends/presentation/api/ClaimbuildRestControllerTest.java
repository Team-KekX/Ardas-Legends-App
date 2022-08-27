package com.ardaslegends.presentation.api;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.ClaimBuild;
import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.presentation.api.ArmyRestController;
import com.ardaslegends.data.presentation.api.ClaimbuildRestController;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.dto.claimbuild.CreateClaimBuildDto;
import com.ardaslegends.data.service.dto.claimbuilds.DeleteClaimbuildDto;
import com.ardaslegends.data.service.dto.claimbuilds.UpdateClaimbuildOwnerDto;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ClaimbuildRestControllerTest {
    MockMvc mockMvc;
    private ClaimBuildService mockClaimbuildService;
    private ClaimbuildRestController claimbuildRestController;

    @BeforeEach
    void setup() {
        mockClaimbuildService = mock(ClaimBuildService.class);
        claimbuildRestController = new ClaimbuildRestController(mockClaimbuildService);
        mockMvc = MockMvcBuilders.standaloneSetup(claimbuildRestController).build();
    }

    @Test
    void ensureCreateClaimbuildWorksProperly() throws Exception {
        log.debug("Testing if createClaimbuild works properly with correct values");

        CreateClaimBuildDto dto = new CreateClaimBuildDto("Nimheria", "91", "Town", "Gondor", 2, 3, 4,
                "huehue:huehue:5", "awdad", "awda", "awdw", "adwada", "Luk");

        ClaimBuild claimBuild = ClaimBuild.builder()
                .name(dto.name())
                .ownedBy(Faction.builder().name(dto.faction()).build()).createdArmies(new ArrayList<>()).specialBuildings(new ArrayList<>())
                .stationedArmies(new ArrayList<>())
                .build();
        when(mockClaimbuildService.createClaimbuild(dto)).thenReturn(claimBuild);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        var result = mockMvc.perform((MockMvcRequestBuilders
                        .post("http://localhost:8080/api/claimbuild/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)))
                .andExpect(status().isOk())
                .andReturn();

        var request = result.getRequest();
        request.setCharacterEncoding("UTF-8");

        log.error(result.getResponse().getContentAsString());
        ClaimBuild response = mapper.readValue(result.getResponse().getContentAsString()
                ,ClaimBuild.class);

        assertThat(response.getName()).isEqualTo(claimBuild.getName());
        assertThat(response.getOwnedBy().getName()).isEqualTo(claimBuild.getOwnedBy().getName());

        log.info("Test passed: createClaimbuild builds the correct response");
    }

    @Test
    void ensureUpdateClaimbuildOwnerWorksProperly() throws Exception {
        log.debug("Testing if updateClaimbuildOwner works properly with correct values");

        UpdateClaimbuildOwnerDto dto = new UpdateClaimbuildOwnerDto("Claimbuild", "Gondor");

        ClaimBuild claimBuild = ClaimBuild.builder()
                .name(dto.claimbuildName())
                .ownedBy(Faction.builder().name(dto.newFaction()).build())
                .build();
        when(mockClaimbuildService.setOwnerFaction(dto)).thenReturn(claimBuild);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        var result = mockMvc.perform((MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/claimbuild/update/claimbuild-faction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)))
                .andExpect(status().isOk())
                .andReturn();

        var request = result.getRequest();
        request.setCharacterEncoding("UTF-8");

        UpdateClaimbuildOwnerDto response = mapper.readValue(request.getContentAsString()
                ,UpdateClaimbuildOwnerDto.class);

        assertThat(response.claimbuildName()).isEqualTo(claimBuild.getName());
        assertThat(response.newFaction()).isEqualTo(claimBuild.getOwnedBy().getName());

        log.info("Test passed: updateClaimbuildOwner builds the correct response");
    }
    @Test
    void ensureDeleteClaimbuildWorksProperly() throws Exception {
        log.debug("Testing if deleteClaimbuild works properly with correct values");

        DeleteClaimbuildDto dto = new DeleteClaimbuildDto("Claimbuild", null, null);

        ClaimBuild claimBuild = ClaimBuild.builder()
                .name(dto.claimbuildName())
                .stationedArmies(List.of(Army.builder().name("Kek1").build(), Army.builder().name("Kek2").build()))
                .createdArmies(List.of(Army.builder().name("Gondr1").build(), Army.builder().name("Gondr2").build()))
                .build();

        when(mockClaimbuildService.deleteClaimbuild(dto)).thenReturn(claimBuild);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        var result = mockMvc.perform((MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/claimbuild/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)))
                .andExpect(status().isOk())
                .andReturn();

        var request = result.getRequest();
        request.setCharacterEncoding("UTF-8");

       DeleteClaimbuildDto response = mapper.readValue(request.getContentAsString()
                ,DeleteClaimbuildDto.class);

        assertThat(response.claimbuildName()).isEqualTo(claimBuild.getName());
        assertThat(response.unstationedArmies()).isEqualTo(claimBuild.getStationedArmies());
        assertThat(response.deletedArmies()).isEqualTo(claimBuild.getCreatedArmies());

        log.info("Test passed: deleteClaimbuild builds the correct response");
    }
}
