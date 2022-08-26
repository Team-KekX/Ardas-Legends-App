package com.ardaslegends.service;

import com.ardaslegends.data.domain.ClaimBuild;
import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.FactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

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

}
