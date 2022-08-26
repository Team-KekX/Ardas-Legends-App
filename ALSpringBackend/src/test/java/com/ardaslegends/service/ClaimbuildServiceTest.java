package com.ardaslegends.service;

import com.ardaslegends.data.domain.ClaimBuild;
import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.dto.claimbuilds.UpdateClaimbuildOwnerDto;
import com.ardaslegends.data.service.exceptions.claimbuild.ClaimBuildServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class ClaimbuildServiceTest {

    private ClaimBuildService claimBuildService;
    private ClaimBuildRepository mockClaimbuildRepository;
    private FactionService mockFactionService;

    private Faction faction;
    private Faction faction2;
    private ClaimBuild claimbuild;

    @BeforeEach
    void setup() {
        mockClaimbuildRepository = mock(ClaimBuildRepository.class);
        mockFactionService = mock(FactionService.class);

        claimBuildService = new ClaimBuildService(mockClaimbuildRepository, mockFactionService);

        faction = Faction.builder().name("Gondor").build();
        faction2 = Faction.builder().name("Mordor").build();

        claimbuild = ClaimBuild.builder().name("Minas Tirith").ownedBy(faction2).build();

        when(mockClaimbuildRepository.findById(claimbuild.getName())).thenReturn(Optional.of(claimbuild));
        when(mockFactionService.getFactionByName(faction.getName())).thenReturn(faction);
        when(mockFactionService.getFactionByName(faction2.getName())).thenReturn(faction2);
    }

    @Test
    void ensureSetOwnerFactionWorksProperly() {
        log.debug("Testing if setOwnerFaction works properly with correct values");

        UpdateClaimbuildOwnerDto dto = new UpdateClaimbuildOwnerDto(claimbuild.getName(), faction.getName());

        when(mockClaimbuildRepository.save(claimbuild)).thenReturn(claimbuild);

        log.debug("Calling setOwnerFaction, expecting no errors");
        var result = claimBuildService.setOwnerFaction(dto);

        assertThat(result).isNotNull();
        assertThat(result.claimbuildName()).isEqualTo(dto.claimbuildName());
        assertThat(result.newFaction()).isEqualTo(dto.newFaction());

        log.info("Test passed: setOwnerFaction works properly with correct values");
    }

    @Test
    void ensureGetClaimbuildByNameThrowsSeWhenPassedNameDoesNotHaveACb() {
        log.debug("Testing if getClaimbuildByName throws Se when passed name does not have a corresponding claimbuild in database");

        String name = "Kek";
        when(mockClaimbuildRepository.findById(name)).thenReturn(Optional.empty());

        log.debug("Calling getClaimbuildByName, expecting Se");
        var result = assertThrows(ClaimBuildServiceException.class, () -> claimBuildService.getClaimBuildByName(name));

        assertThat(result.getMessage()).isEqualTo(ClaimBuildServiceException.noCbWithName(name).getMessage());
        log.info("Test passed: getClaimbuildByName correctly throws Se when no cb entry with passed name in database");
    }
}
