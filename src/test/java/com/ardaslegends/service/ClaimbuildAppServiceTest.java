package com.ardaslegends.service;

import com.ardaslegends.presentation.api.response.region.RegionResponse;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.repository.ProductionSiteRepository;
import com.ardaslegends.repository.applications.claimbuildapp.ClaimbuildApplicationRepository;
import com.ardaslegends.repository.claimbuild.ClaimbuildRepository;
import com.ardaslegends.repository.faction.FactionRepository;
import com.ardaslegends.repository.player.PlayerRepository;
import com.ardaslegends.repository.region.RegionRepository;
import com.ardaslegends.service.applications.ClaimbuildApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

import java.time.Clock;

import static org.mockito.Mockito.mock;

@Slf4j
public class ClaimbuildAppServiceTest {

    private ClaimbuildApplicationService cbAppService;


    private ClaimbuildApplicationRepository cbAppRepository;
    private ClaimbuildRepository mockClaimBuildRepository;
    private PlayerRepository mockPlayerRepository;
    private FactionRepository mockFactionRepository;
    private RegionRepository mockRegionRepository;
    private ProductionSiteRepository mockProductionSiteRepository;
    private BotProperties mockBotProperties;
    private Clock mockClock;

    @BeforeEach
    void setup() {
        cbAppRepository = mock(ClaimbuildApplicationRepository.class);
        mockClaimBuildRepository = mock(ClaimbuildRepository.class);
        mockFactionRepository = mock(FactionRepository.class);
        mockRegionRepository = mock(RegionRepository.class);
        mockPlayerRepository = mock(PlayerRepository.class);
        mockProductionSiteRepository = mock(ProductionSiteRepository.class);
        mockBotProperties = mock(BotProperties.class);
        mockClock = mock(Clock.class);

        cbAppService = new ClaimbuildApplicationService(cbAppRepository, mockClaimBuildRepository, mockPlayerRepository,
                mockFactionRepository, mockRegionRepository, mockProductionSiteRepository, mockBotProperties, mockClock);
    }
}
