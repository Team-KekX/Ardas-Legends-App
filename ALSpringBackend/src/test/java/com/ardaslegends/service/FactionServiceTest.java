package com.ardaslegends.service;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.UpdateFactionLeaderDto;
import com.ardaslegends.data.service.exceptions.FactionServiceException;
import com.ardaslegends.data.service.exceptions.PlayerServiceException;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class FactionServiceTest {

    private FactionRepository mockFactionRepository;

    private PlayerService mockPlayerService;
    private FactionService factionService;
    @BeforeEach
    void setup() {
        mockFactionRepository = mock(FactionRepository.class);
        mockPlayerService = mock(PlayerService.class);
        factionService = new FactionService(mockFactionRepository, mockPlayerService);
    }

    @Test
    void ensureSetFactionLeaderWorksProperlyWithCorrectValues() {
        log.debug("Testing if setFactionLeader works properly with correct values ");

        UpdateFactionLeaderDto dto = new UpdateFactionLeaderDto("Kek", "kek");

        Faction gondor = Faction.builder().name("Gondrr").build();
        when(mockFactionRepository.findById(dto.factionName())).thenReturn(Optional.of(gondor));

        Player player = Player.builder().ign("mirak551").faction(gondor).rpChar(new RPChar()).build();
        when(mockPlayerService.getPlayerByDiscordId(dto.targetDiscordId())).thenReturn(player);

        when(mockFactionRepository.save(gondor)).thenReturn(gondor);

        log.debug("Calling factionService.setFactionLeader, expecting no errors");
        var result = factionService.setFactionLeader(dto);

        assertThat(result.getLeader()).isEqualTo(player);
        log.info("Test passed: setFactionLeader works correctly!");
    }
    @Test
    void ensureSetFactionLeaderThrowsSeWhenPlayerIsNotInTheSameFactionAsTheTargetFaction() {
        log.debug("Testing if setFactionLeader properly throws Se when player is not in the same faction as the target faction");

        UpdateFactionLeaderDto dto = new UpdateFactionLeaderDto("Kek", "kek");

        Player player = Player.builder().ign("mirak551").faction(Faction.builder().name("WrongFactioN").build()).build();
        when(mockPlayerService.getPlayerByDiscordId(dto.targetDiscordId())).thenReturn(player);

        Faction gondor = Faction.builder().name("Gondrr").build();
        when(mockFactionRepository.findById(dto.factionName())).thenReturn(Optional.of(gondor));

        log.debug("Calling factionService.setFactionLeader, expecting Se");
        var result = assertThrows(FactionServiceException.class, () -> factionService.setFactionLeader(dto));

        assertThat(result.getMessage()).isEqualTo(FactionServiceException.factionLeaderMustBeOfSameFaction().getMessage());
        log.info("Test passed: setFactionLeader correctly throws Se when player is not in the same faction as target faction");
    }
    @Test
    void ensureSetFactionLeaderThrowsSeWhenPlayerDoesNotHaveAnRpChar() {
        log.debug("Testing if setFactionLeader properly throws Se when player does not have an rpchar");

        UpdateFactionLeaderDto dto = new UpdateFactionLeaderDto("Kek", "kek");

        Faction gondor = Faction.builder().name("Gondrr").build();
        when(mockFactionRepository.findById(dto.factionName())).thenReturn(Optional.of(gondor));

        Player player = Player.builder().ign("mirak551").faction(gondor).build();
        when(mockPlayerService.getPlayerByDiscordId(dto.targetDiscordId())).thenReturn(player);

        log.debug("Calling factionService.setFactionLeader, expecting Se");
        var result = assertThrows(PlayerServiceException.class, () -> factionService.setFactionLeader(dto));

        assertThat(result.getMessage()).isEqualTo(PlayerServiceException.noRpChar().getMessage());
        log.info("Test passed: setFactionLeader correctly throws Se when player does not have an rpchar");
    }

    @Test
    void ensureGetByFactionNameWorksProperly() {
        // Assign
        String name = "Mordor";

        when(mockFactionRepository.findById(name)).thenReturn(Optional.of(Faction.builder().name(name).build()));

        // Act
        var result = factionService.getFactionByName(name);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
    }

    @Test
    void ensureGetByFactionNameThrowsIllegalArgumentExceptionWhenNameIsBlank() {
        // Assign
        String name = " ";

        // Assert
        var result = assertThrows(IllegalArgumentException.class, () -> factionService.getFactionByName(name));

        assertThat(result.getMessage()).isEqualTo("Faction name must not be blank!");

    }

    @Test
    void ensureGetByFactionNameThrowsServiceExceptionWhenDatabaseDown() {
        // Assign
        String name = "Mordor";

        PersistenceException pEx = new PersistenceException("Database down");

        when(mockFactionRepository.findById(name)).thenThrow(pEx);

        // Assert
        var result = assertThrows(ServiceException.class, () -> factionService.getFactionByName(name));

        assertThat(result.getCause()).isEqualTo(pEx);
    }

    @Test
    void ensureGetByFactionNameThrowsSeWhenFetchedFactionIsEmpty() {
        log.debug("Testing if getByFaction in FactionService throws Se when Fetched Faction is Empty");

        String name = "Mordor";

        when(mockFactionRepository.findById(name)).thenReturn(Optional.empty());

        // Assert
        var result = assertThrows(FactionServiceException.class, () -> factionService.getFactionByName(name));

        assertThat(result.getMessage()).isEqualTo(FactionServiceException.noFactionWithNameFound(name).getMessage());
        log.info("Test passed: getFactionByName throws Se when no faction found in database");
    }

}
