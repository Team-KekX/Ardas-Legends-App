package com.ardaslegends.service;

import com.ardaslegends.repository.BattleRepository;
import com.ardaslegends.repository.MovementRepository;
import com.ardaslegends.repository.war.WarRepository;
import com.ardaslegends.service.war.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

@Slf4j
public class BattleServiceTest {
    private BattleRepository mockBattleRepository;
    private ArmyService mockArmyService;
    private PlayerService mockPlayerService;
    private ClaimBuildService mockClaimBuildService;
    private WarRepository mockWarRepository;
    private MovementRepository mockMovementRepository;
    private BattleService battleService;



    @BeforeEach
    void setup(){
        mockBattleRepository = mock(BattleRepository.class);
        mockWarRepository = mock(WarRepository.class);
        mockMovementRepository = mock(MovementRepository.class);
        mockArmyService = mock(ArmyService.class);
        mockPlayerService = mock(PlayerService.class);
        mockClaimBuildService = mock(ClaimBuildService.class);
        battleService = new BattleService(mockBattleRepository,mockArmyService,mockPlayerService,mockClaimBuildService,mockWarRepository,mockMovementRepository);



    }


    @Test
    void ensureCreateBattleWorks(){

    }
}
