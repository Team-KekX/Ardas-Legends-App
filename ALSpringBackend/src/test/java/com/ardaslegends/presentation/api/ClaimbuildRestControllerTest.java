package com.ardaslegends.presentation.api;

import com.ardaslegends.data.domain.*;
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
import java.util.stream.Collectors;

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
    private Faction faction;
    private Region region1;
    private Region region2;
    private UnitType unitType;
    private Unit unit;
    private RPChar rpchar;
    private Player player;
    private Army army1;
    private Army army2;
    private Army army3;
    private Movement movement;
    private ClaimBuild claimBuild;
    @BeforeEach
    void setup() {
        mockClaimbuildService = mock(ClaimBuildService.class);
        claimbuildRestController = new ClaimbuildRestController(mockClaimbuildService);
        mockMvc = MockMvcBuilders.standaloneSetup(claimbuildRestController).build();

        region1 = Region.builder().id("90").build();
        region2 = Region.builder().id("91").build();
        unitType = UnitType.builder().unitName("Gondor Archer").tokenCost(1.5).build();
        unit = Unit.builder().unitType(unitType).army(army1).amountAlive(5).count(10).build();
        faction = Faction.builder().name("Gondor").allies(new ArrayList<>()).build();
        claimBuild = ClaimBuild.builder().name("Nimheria").siege("Ram, Trebuchet, Tower").region(region1).ownedBy(faction).specialBuildings(List.of(SpecialBuilding.HOUSE_OF_HEALING)).stationedArmies(List.of()).build();
        rpchar = RPChar.builder().name("Belegorn").currentRegion(region1).build();
        player = Player.builder().discordID("1234").faction(faction).rpChar(rpchar).build();
        army1 = Army.builder().name("Knights of Gondor").armyType(ArmyType.ARMY).faction(faction).units(List.of(unit)).freeTokens(30 - unit.getCount() * unitType.getTokenCost()).currentRegion(region2).stationedAt(claimBuild).sieges(new ArrayList<>()).build();
        army2 = Army.builder().name("Knights of Luk").armyType(ArmyType.ARMY).faction(faction).units(List.of(unit)).freeTokens(30 - unit.getCount() * unitType.getTokenCost()).currentRegion(region2).stationedAt(claimBuild).sieges(new ArrayList<>()).build();
        army3 = Army.builder().name("Knights of Kek").armyType(ArmyType.ARMY).faction(faction).units(List.of(unit)).freeTokens(30 - unit.getCount() * unitType.getTokenCost()).currentRegion(region2).stationedAt(claimBuild).sieges(new ArrayList<>()).build();
        movement =  Movement.builder().isCharMovement(false).isCurrentlyActive(true).army(army1).path(Path.builder().path(List.of("90", "91")).build()).build();

        claimBuild.setStationedArmies(List.of(army1));
        claimBuild.setCreatedArmies(List.of(army2, army3));
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
        when(mockClaimbuildService.createClaimbuild(dto, true)).thenReturn(claimBuild);

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

        var request = result.getResponse();
        request.setCharacterEncoding("UTF-8");

        log.error(result.getResponse().getContentAsString());
        ClaimBuild response = mapper.readValue(result.getResponse().getContentAsString()
                ,ClaimBuild.class);

        assertThat(response.getName()).isEqualTo(claimBuild.getName());
        assertThat(response.getOwnedBy().getName()).isEqualTo(claimBuild.getOwnedBy().getName());

        log.info("Test passed: createClaimbuild builds the correct response");
    }

    @Test
    void ensureCreateClaimbuildWorksProperlyWhenUpdatingCb() throws Exception {
        log.debug("Testing if createClaimbuild works properly with correct values");

        CreateClaimBuildDto dto = new CreateClaimBuildDto("Nimheria", "91", "Town", "Gondor", 2, 3, 4,
                "huehue:huehue:5", "awdad", "awda", "awdw", "adwada", "Luk");

        ClaimBuild claimBuild = ClaimBuild.builder()
                .name(dto.name())
                .ownedBy(Faction.builder().name(dto.faction()).build()).createdArmies(new ArrayList<>()).specialBuildings(new ArrayList<>())
                .stationedArmies(new ArrayList<>())
                .build();
        when(mockClaimbuildService.createClaimbuild(dto, false)).thenReturn(claimBuild);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        var result = mockMvc.perform((MockMvcRequestBuilders
                        .patch("http://localhost:8080/api/claimbuild/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)))
                .andExpect(status().isOk())
                .andReturn();

        var request = result.getResponse();
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

        var request = result.getResponse();
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

        DeleteClaimbuildDto dto = new DeleteClaimbuildDto(claimBuild.getName(), null, null);

        when(mockClaimbuildService.deleteClaimbuild(dto)).thenReturn(this.claimBuild);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);
        String test = ow.writeValueAsString(new DeleteClaimbuildDto(
                claimBuild.getName(),
                claimBuild.getStationedArmies().stream().map(Army::getName).collect(Collectors.toList()),
                claimBuild.getCreatedArmies().stream().map(Army::getName).collect(Collectors.toList()))
        );
        System.out.println(test);

        var result = mockMvc.perform((MockMvcRequestBuilders
                        .delete("http://localhost:8080/api/claimbuild/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)))
                .andExpect(status().isOk())
                .andReturn();

        var request = result.getResponse();
        request.setCharacterEncoding("UTF-8");

        System.out.println(request.getContentAsString());
        DeleteClaimbuildDto response = mapper.readValue(request.getContentAsString()
                ,DeleteClaimbuildDto.class);


        assertThat(response.claimbuildName()).isEqualTo(claimBuild.getName());
        assertThat(response.unstationedArmies()).isEqualTo(claimBuild.getStationedArmies().stream().map(Army::getName).collect(Collectors.toList()));
        assertThat(response.deletedArmies()).isEqualTo(claimBuild.getCreatedArmies().stream().map(Army::getName).collect(Collectors.toList()));

        log.info("Test passed: delete Claimbuild builds the correct response");
    }
}
