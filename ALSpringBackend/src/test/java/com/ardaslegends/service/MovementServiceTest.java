package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.Pathfinder;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
import com.ardaslegends.data.service.dto.player.rpchar.UpdateRpCharDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class MovementServiceTest {

    private MovementRepository mockMovementRepository;
    private RegionRepository mockRegionRepository;

    private PlayerService mockPlayerService;
    private Pathfinder mockPathfinder;

    private MovementService movementService;

    @BeforeEach
    void setup() {
        mockMovementRepository = mock(MovementRepository.class);
        mockRegionRepository = mock(RegionRepository.class);
        mockPlayerService = mock(PlayerService.class);
        mockPathfinder = mock(Pathfinder.class);
        movementService = new MovementService(mockMovementRepository, mockRegionRepository, mockPlayerService, mockPathfinder);
    }

    @Test
    void ensureMoveRpCharWorks() {
        log.debug("Testing if createRpCharMovement works with valid values!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("90").build();
        Region toRegion = Region.builder().id("92").build();
        RPChar rpChar = RPChar.builder().name("Belegorn Arnorion").currentRegion(fromRegion).build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(rpChar).build();
        Path endPath = Path.builder().path(List.of("91", "92")).cost(2).build();

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", toRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerService.getPlayerByDiscordId("1234")).thenReturn(player);
        when(mockRegionRepository.findById(toRegion.getId())).thenReturn(Optional.of(toRegion));
        when(mockPathfinder.findShortestWay(fromRegion, toRegion, player, true)).thenReturn(endPath);

        //Act
        Movement createdMovement = movementService.createRpCharMovement(dto);

        //Assert
        log.debug("Starting asserts");
        assertThat(createdMovement.getPlayer()).isEqualTo(player);
        assertThat(createdMovement.getIsCharMovement()).isTrue();
        assertThat(createdMovement.getStartTime().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(createdMovement.getEndTime().toLocalDate()).isEqualTo(LocalDate.now().plusDays(endPath.getCost()));
        assertThat(createdMovement.getIsAccepted()).isFalse();
        assertThat(createdMovement.getArmy()).isNull();

        log.info("Test passed: createRpCharMovement works with valid values!");
    }

    @Test
    void ensureMoveRpCharThrowsSEWhenNoRpChar() {
        log.debug("Testing if createRpCharMovement throws IllegalArgumentException when player has no Rp Char!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("90").build();
        Region toRegion = Region.builder().id("92").build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(null).build();

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", toRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerService.getPlayerByDiscordId("1234")).thenReturn(player);

        //Act
        var exception = assertThrows(ServiceException.class, () -> movementService.createRpCharMovement(dto));

        //Assert
        assertThat(exception.getMessage()).isEqualTo(ServiceException.noRpChar().getMessage());

        log.info("Test passed: createRpCharMovement throws IllegalArgumentException when player has no Rp Char!");
    }


    @Test
    void ensureMoveRpCharThrowsIAEWhenEndRegionDoesNotExists() {
        log.debug("Testing if createRpCharMovement throws IllegalArgumentException when end Region does not exist!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("90").build();
        Region toRegion = Region.builder().id("92").build();
        RPChar rpChar = RPChar.builder().name("Belegorn Arnorion").currentRegion(fromRegion).build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(rpChar).build();

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", toRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerService.getPlayerByDiscordId("1234")).thenReturn(player);
        when(mockRegionRepository.findById(toRegion.getId())).thenReturn(Optional.empty());

        //Act
        var exception = assertThrows(IllegalArgumentException.class, () -> movementService.createRpCharMovement(dto));

        //Assert
        assertThat(exception.getMessage()).isEqualTo("The region %s does not exist!".formatted(dto.toRegion()));

        log.info("Test passed: createRpCharMovement throws IllegalArgumentException when player has no Rp Char!");
    }

    @Test
    void ensureMoveRpCharThrowsSEWhenBoundToArmy() {
        log.debug("Testing if createRpCharMovement throws ServiceException when Char is bound to Army!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("90").build();
        Region toRegion = Region.builder().id("92").build();
        RPChar rpChar = RPChar.builder().name("Belegorn Arnorion").currentRegion(fromRegion).build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(rpChar).build();
        Army army = Army.builder().name("Army of Gondor").boundTo(player).currentRegion(fromRegion).build();
        rpChar.setBoundTo(army);

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", toRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerService.getPlayerByDiscordId("1234")).thenReturn(player);
        when(mockRegionRepository.findById(toRegion.getId())).thenReturn(Optional.empty());

        //Act
        var exception = assertThrows(ServiceException.class, () -> movementService.createRpCharMovement(dto));

        //Assert
        log.debug("Asserting that createRpCharMovement throws ServiceException");
        assertThat(exception.getMessage()).isEqualTo(ServiceException.cannotMoveRpCharBoundToArmy(rpChar, army).getMessage());

        log.info("Test passed: createRpCharMovement throws ServiceException when Char is bound to Army!");
    }
}
