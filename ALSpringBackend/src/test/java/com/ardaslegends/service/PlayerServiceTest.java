package com.ardaslegends.service;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.domain.Region;
import com.ardaslegends.data.repository.PlayerRepository;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.*;
import com.ardaslegends.data.service.dto.player.rpchar.CreateRPCharDto;
import com.ardaslegends.data.service.dto.player.rpchar.UpdateRpCharDto;
import com.ardaslegends.data.service.dto.player.rpchar.UpdateRpCharNameDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.external.MojangApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
@Slf4j
public class PlayerServiceTest {

    private PlayerRepository mockPlayerRepository;
    private FactionService mockFactionService;

    private MojangApiService mockMojangApiService;
    private PlayerService playerService;

    @BeforeEach
    void setup() {
        mockPlayerRepository = mock(PlayerRepository.class);
        mockFactionService = mock(FactionService.class);
        mockMojangApiService = mock(MojangApiService.class);
        playerService = new PlayerService(mockPlayerRepository, mockFactionService, mockMojangApiService);
    }

    // Create Method Tests
    @Test
    void ensureCreatePlayerWorksProperly() {

        // Assign
        CreatePlayerDto createPlayerDto = new CreatePlayerDto("mirak441", "hellopersonalidk", "Mordor");
        Player p = Player.builder().ign(createPlayerDto.ign()).discordID(createPlayerDto.discordID()).build();

        when(mockFactionService.getFactionByName(createPlayerDto.faction())).thenReturn(Faction.builder().name(createPlayerDto.faction()).build());

        when(mockPlayerRepository.findById(createPlayerDto.ign())).thenReturn(Optional.empty());
        when(mockPlayerRepository.findByDiscordID(createPlayerDto.discordID())).thenReturn(Optional.empty());
        when(mockPlayerRepository.save(any())).thenReturn(p);
        when(mockMojangApiService.getUUIDByIgn(createPlayerDto.ign())).thenReturn(new UUIDConverterDto(createPlayerDto.ign(), "235432456235"));

        // Act
        var result = playerService.createPlayer(createPlayerDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(p);
        assertThat(result.getDiscordID()).isEqualTo(createPlayerDto.discordID());
        assertThat(result.getIgn()).isEqualTo(createPlayerDto.ign());

    }

    @Test
    void ensureCreatePlayerThrowsServiceExceptionFindingDatabaseEntryWithSameIgn() {
        // Assign
        CreatePlayerDto dto = new CreatePlayerDto("mirak", "karim", "Mordor");
        when(mockFactionService.getFactionByName(dto.faction())).thenReturn(Faction.builder().name(dto.faction()).build());
        when(mockPlayerRepository.findByDiscordID(dto.discordID())).thenReturn(Optional.of(Player.builder().ign(dto.ign()).discordID(dto.discordID()).build()));
        when(mockPlayerRepository.findPlayerByIgn(dto.ign())).thenReturn(Optional.of(Player.builder().ign(dto.ign()).discordID(dto.discordID()).build()));
        // Assert
        var result = assertThrows(ServiceException.class, () -> playerService.createPlayer(dto));

        assertThat(result.getMessage()).contains("due to it already existing!");
    }
    @Test
    void ensureCreatePlayerThrowsServiceExceptionFindingDatabaseEntryWithSameDiscordId() {
        // Assign
        CreatePlayerDto dto = new CreatePlayerDto("mirak", "karim", "Mordor");
        when(mockFactionService.getFactionByName(dto.faction())).thenReturn(Faction.builder().name(dto.faction()).build());
        when(mockPlayerRepository.findByDiscordID(dto.discordID())).thenReturn(Optional.of(Player.builder().ign(dto.ign()).discordID(dto.discordID()).build()));
        // Assert
        var result = assertThrows(ServiceException.class, () -> playerService.createPlayer(dto));

        assertThat(result.getMessage()).contains("due to it already existing!");
    }


    // Create RPChar Tests

    @Test
    void ensureCreateRPCharWorksProperly() {
        // Assign
        // Title is max length 25 long
        String title = "aaaaaaaaaaaaaaaaaaaaaaaaa";
        CreateRPCharDto dto = new CreateRPCharDto("MiraksDiscordId", "ActualPrinceOfDolAmroth",
                title, "Something Gondolin and Galvorn", true);

        Faction faction = Faction.builder().name("Gondor").homeRegion(Region.builder().id("1").build()).build();
        Player player = Player.builder().discordID(dto.discordId()).faction(faction).build();

        // Find corresponding player
        when(mockPlayerRepository.findByDiscordID(dto.discordId())).thenReturn(Optional.of(player));
        // No other player with the same RPChar name
        when(mockPlayerRepository.findPlayerByRpChar(dto.rpCharName())).thenReturn(Optional.empty());
        when(mockPlayerRepository.save(player)).thenReturn(player);

        // Act
        var result = playerService.createRoleplayCharacter(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(dto.rpCharName());
        assertThat(result.getPvp()).isEqualTo(dto.pvp());


    }

    @Test
    void ensureCreateRPCharThrowsIAEWhenTitleIsTooLong() {
        // Assign
        String title26Length = "aaaaaaaaaaaaaaaaaaaaaaaaaa";
        CreateRPCharDto dto = new CreateRPCharDto("MiraksDiscordId", "ActualPrinceOfDolAmroth",
                title26Length, "Something Gondolin and Galvorn", true);

        // Assert

        var result = assertThrows(IllegalArgumentException.class, () -> playerService.createRoleplayCharacter(dto));
    }

    @Test
    void ensureCreateRPCharThrowsIAEWhenPlayerAlreadyHasAnRPChar() {
        // Assign
        CreateRPCharDto dto = new CreateRPCharDto("MiraksDiscordId", "ActualPrinceOfDolAmroth",
                "Prince of Dol Amroth", "Something Gondolin and Galvorn", true);
        RPChar alreadySavedRPChar = RPChar.builder().name("Gondorian Knight").build();
        Player player = Player.builder().discordID(dto.discordId()).rpChar(alreadySavedRPChar).build();

        when(mockPlayerRepository.findByDiscordID(dto.discordId())).thenReturn(Optional.of(player));

        // Assert
        var result = assertThrows(IllegalArgumentException.class, () -> playerService.createRoleplayCharacter(dto));

        assertThat(result.getMessage()).contains("already has an RPChar");
    }

    @Test
    void ensureCreateRPCharThrowsIAEWhenAPlayerAlreadyHasAnRPCharWithSameName() {
        // Assign
        CreateRPCharDto dto = new CreateRPCharDto("MiraksDiscordId", "ActualPrinceOfDolAmroth",
                "Prince of Dol Amroth", "Something Gondolin and Galvorn", true);

        Player player = Player.builder().discordID(dto.discordId()).build();

        when(mockPlayerRepository.findByDiscordID(dto.discordId())).thenReturn(Optional.of(player));
        // Simulate finding a player with an rpchar that has the same name
        when(mockPlayerRepository.findPlayerByRpChar(dto.rpCharName())).thenReturn(Optional.of(new Player()));

        // Assert
        var result = assertThrows(IllegalArgumentException.class, () -> playerService.createRoleplayCharacter(dto));

        assertThat(result.getMessage()).contains("Roleplay Character with name");
    }

    // ----------------------------------------------------------- Read Method Tests

    @Test
    void ensureGetPlayerByIgnWorksProperly() {
        //Assign
        String ign = "aned";
        Player p = Player.builder().ign(ign).build();
        when(mockPlayerRepository.findPlayerByIgn(ign)).thenReturn(Optional.of(p));

        // Act
        var result = playerService.getPlayerByIgn(ign);

        // Assert
        assertThat(result).isEqualTo(p);
    }

    @Test
    void ensureGetPlayerByIgnThrowsServiceExceptionWhenNoRecordFound() {
        //Assign
        String ign = "aned";
        Player p = Player.builder().ign(ign).build();
        when(mockPlayerRepository.findById(ign)).thenReturn(Optional.empty());

        // Assert
        var result = assertThrows(ServiceException.class, () -> playerService.getPlayerByIgn(ign));

        assertThat(result.getMessage()).contains("No record of type");
    }

    @Test
    void ensureGetPlayerByDiscordIdWorksProperly() {
        //Assign
        String discordId = "hel";
        Player p = Player.builder().discordID(discordId).build();
        when(mockPlayerRepository.findByDiscordID(discordId)).thenReturn(Optional.of(p));

        // Act
        var result = playerService.getPlayerByDiscordId(discordId);

        // Assert
        assertThat(result).isEqualTo(p);
    }

    @Test
    void ensureGetPlayerByDiscordIdThrowsServiceExceptionWhenNoRecordFound() {
        //Assign
        String discordId = "hel";
        Player p = Player.builder().discordID(discordId).build();
        when(mockPlayerRepository.findByDiscordID(discordId)).thenReturn(Optional.empty());

        // Assert
        var result = assertThrows(ServiceException.class, () -> playerService.getPlayerByDiscordId(discordId));

        assertThat(result.getMessage()).contains("No record of type");
    }

    // ------------------------------------------------ Update Method tests

    // Update Faction

    @Test
    void ensureUpdatePlayerFactionWorks() {
        log.debug("Testing if updatePlayerFaction works with valid values...");

        //Assign

        log.trace("Initializing Factions...");
        Faction gondor = Faction.builder().name("Gondor").build();
        Faction mordor = Faction.builder().name("Mordor").build();

        log.trace("Initializing Player");
        Player player = Player.builder().ign("Louktrounic").discordID("123456789").faction(mordor).build();

        log.trace("Initializing PlayerDTO");
        UpdatePlayerFactionDto updateDto = new UpdatePlayerFactionDto(player.getDiscordID(), gondor.getName());

        log.trace("Initializing mocked methods");
        when(mockPlayerRepository.findByDiscordID(player.getDiscordID())).thenReturn(Optional.of(player));
        when(mockFactionService.getFactionByName(gondor.getName())).thenReturn(gondor);
        when(mockPlayerRepository.save(player)).thenReturn(player);

        //Act

        log.debug("Executing updatePlayerFaction");
        playerService.updatePlayerFaction(updateDto);

        // Assert

        log.debug("Asserting that Faction got updated");
        assertThat(player.getFaction()).isEqualTo(gondor);

        log.info("Test passed: updatePlayerFaction updates Player's faction when given valid values!");
    }

    // UpdateIgn

    @Test
    void ensureUpdateIgnWorks() {
        log.debug("Testing if updateIgn works with valid values...");

        //Assign

        log.trace("Initializing Player");
        String ign = "mirak";
        String newIgn = "Louktrounic";
        String uuid = "1234";
        String newUuid = "9876";
        String discordId = "1220";
        Player player = Player.builder().ign(ign).discordID(discordId).uuid(uuid).build();

        log.trace("Initializing updatePlayerDto");
        UpdatePlayerIgnDto updateDto = new UpdatePlayerIgnDto(newIgn, discordId);

        log.trace("Initializing UUIDConverterDto");
        UUIDConverterDto uuidConverterDto = new UUIDConverterDto(ign, newUuid);

        log.trace("Initializing mocked methods");
        when(mockMojangApiService.getUUIDByIgn(newIgn)).thenReturn(uuidConverterDto);
        when(mockPlayerRepository.findPlayerByIgn(newIgn)).thenReturn(Optional.empty());
        when(mockPlayerRepository.findByDiscordID(discordId)).thenReturn(Optional.of(player));
        when(mockPlayerRepository.save(player)).thenReturn(player);

        //Act

        log.debug("Executing updateIgn...");
        var result = playerService.updateIgn(updateDto);

        //Assert

        log.debug("Asserting that IGN has updated");
        assertThat(result.getIgn()).isEqualTo(newIgn);
        assertThat(result.getUuid()).isEqualTo(newUuid);

        log.info("Test passed: updateIgn works with valid values!");
    }

    @Test
    void ensureUpdateIgnThrowsIAEWhenIgnExistsAlready() {
        log.debug("Testing if updateIgn throws IllegalArgumentException when IGN is taken already...");

        //Assign

        log.trace("Intitializing player");
        String newIgn = "mirak";
        Player player = Player.builder().ign("Vernoun").build();
        Player existingPlayer = Player.builder().ign(newIgn).build();

        log.trace("Initializing updatePlayerDto");
        UpdatePlayerIgnDto updateDto = new UpdatePlayerIgnDto (newIgn, "discordId");

        log.trace("Initializing mocked methods");
        when(mockPlayerRepository.findPlayerByIgn(newIgn)).thenReturn(Optional.of(player));
        when(mockPlayerRepository.findByDiscordID(updateDto.discordId())).thenReturn(Optional.of(player));

        //Act / Assert

        log.debug("Executing updateIgn...");
        log.debug("Asserting that IllegalArgumentException is thrown with already taken ign");
        assertThrows(IllegalArgumentException.class, () -> playerService.updateIgn(updateDto));

        log.info("Test passed: updateIgn throws IllegalArgumentException when IGN is taken already!");
    }

    // Update Discord Id

    @Test
    void ensureUpdateDiscordIdWorksProperly() {
        log.debug("Testing if updating discord id works properly with correct values");

        // Assign
        log.trace("Initializing UpdateDiscordId Dto");
        UpdateDiscordIdDto dto = new UpdateDiscordIdDto("RandomOldId", "RandomNewId");

        log.trace("Initializing Player Object");
        Player player = Player.builder().discordID(dto.oldDiscordId()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerRepository.findByDiscordID(dto.oldDiscordId())).thenReturn(Optional.of(player));
        when(mockPlayerRepository.findByDiscordID(dto.newDiscordId())).thenReturn(Optional.empty());
        when(mockPlayerRepository.save(player)).thenReturn(player);

        // Act
        var result = playerService.updateDiscordId(dto);

        // Assert
        assertThat(player.getDiscordID()).isEqualTo(dto.newDiscordId());

    }

    @Test
    void ensureUpdateDiscordIdThrowsIllegalArgumentWhenPlayerWithNewDiscordIdAlreadyExists() {
        log.debug("Testing if updating discord id throws IAE when player with new DiscordId already exists");

        // Assign
        log.trace("Initializing UpdateDiscordId Dto");
        UpdateDiscordIdDto dto = new UpdateDiscordIdDto("RandomOldId", "RandomNewId");

        log.trace("Initializing Player Object");
        Player player = Player.builder().discordID(dto.oldDiscordId()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerRepository.findByDiscordID(dto.oldDiscordId())).thenReturn(Optional.of(player));
        when(mockPlayerRepository.findByDiscordID(dto.newDiscordId())).thenReturn(Optional.of(new Player()));

        // Act and Assert
        log.debug("Executing updateDiscordId");
        log.debug("Asserting that IllegalArgumentException will be thrown");
        var result = assertThrows(IllegalArgumentException.class, () -> playerService.updateDiscordId(dto));
    }

    // Update RpChar Name

    @Test
    void ensureUpdateRpCharName() {
        log.debug("Testing if updating the name of a rp character works with correct values");

        // Assign
        log.trace("Initializing Dto");
        UpdateRpCharDto dto = new UpdateRpCharDto("RandomId", "RandomName", null, null, null);

        log.trace("Initializng RpChar Object");
        RPChar rpChar = RPChar.builder().name("OtherName").build();

        log.trace("Initializing Player object");
        Player player = Player.builder().discordID(dto.discordId()).rpChar(rpChar).build();

        log.trace("Initializing mock methods");
        when(mockPlayerRepository.findByDiscordID(dto.discordId())).thenReturn(Optional.of(player));
        when(mockPlayerRepository.findPlayerByRpChar(dto.charName())).thenReturn(Optional.empty());
        when(mockPlayerRepository.save(player)).thenReturn(player);

        // Act
        log.trace("Executing updateCharacterName method");
        var result = playerService.updateCharacterName(dto);

        // Assert
        log.trace("Asserting that the rpchar object has the new name");
        assertThat(result.getName()).isEqualTo(dto.charName());
    }

    @Test
    void ensureUpdateCharacterNameThrowsIllegalArgumentWhenRpCharNameIsAlreadyTaken() {
        log.debug("Testing if update character name correctly throws IAE when new name is already taken");

        // Assign
        log.trace("Initializing Dto");
        UpdateRpCharDto dto = new UpdateRpCharDto("RandomId", "RandomName", null, null, null);

        log.trace("Initializng RpChar Object");
        RPChar rpChar = RPChar.builder().name("OtherName").build();

        log.trace("Initializing Player object");
        Player player = Player.builder().discordID(dto.discordId()).rpChar(rpChar).build();

        log.trace("Initializing mock methods");
        when(mockPlayerRepository.findByDiscordID(dto.discordId())).thenReturn(Optional.of(player));
        when(mockPlayerRepository.findPlayerByRpChar(dto.charName())).thenReturn(Optional.of(player));

        // Act
        log.trace("Executing updateCharacterName");
        log.trace("Asserting that the function call throws a ServiceExceptioN");
        var result = assertThrows(IllegalArgumentException.class, () -> playerService.updateCharacterName(dto));

        log.trace("Asserting that the message is correct");
        assertThat(result.getMessage()).contains("RpChar Name is already taken!");
    }

    //Update RpChar Title

    @Test
    void ensureUpdateRpCharTitleWorks() {
      //TODO
    }

    // ------------------------------------------------------------ Delete Methods

    // Delete Player

    @Test
    void ensureDeletePlayerWorksProperly() {
        log.debug("Trying to test if delete player works properly");

        // Assign
        log.trace("Initializing Dto");
        DeletePlayerOrRpCharDto dto = new DeletePlayerOrRpCharDto("RandomID");

        log.trace("Initialize player object that will simulate the deletion");
        Player player = Player.builder().discordID(dto.discordId()).build();

        log.trace("Initialize mock method");
        when(mockPlayerRepository.findByDiscordID(dto.discordId())).thenReturn(Optional.of(player));

        // Act
        log.debug("Executing deletePlayer..");
        var result = playerService.deletePlayer(dto);

        // Assert
        log.debug("Asserting that result player object is equal to player object that has been initialized earlier");
        assertThat(result).isEqualTo(player);
    }

    // Delete RpChar

    @Test
    void ensureDeleteRpCharWorksProperly() {
        log.debug("Trying to test if delete rpchar works properly");

        // Assign
        log.trace("Initialize Dto");
        DeletePlayerOrRpCharDto dto = new DeletePlayerOrRpCharDto("RandomId");

        log.trace("Initialize rpchar object that will simulate the deletion");
        RPChar rpChar = RPChar.builder().name("RandomChar").build();

        log.trace("Initialize player object with above rpchar");
        Player player = Player.builder().discordID(dto.discordId()).rpChar(rpChar).build();

        log.trace("Initialize mock methods");
        when(mockPlayerRepository.findByDiscordID(dto.discordId())).thenReturn(Optional.of(player));

        // Act
        log.debug("Executing deleteRpChar");
        var result = playerService.deleteRpChar(dto);

        // Assert
        log.debug("Asserting that the returned RpChar is the same as the previously initialized one");
        assertThat(result).isEqualTo(rpChar);
    }

    @Test
    void ensureDeleteRpCharThrowsIllegalArgumentWhenNoRpCharPresent() {
        log.debug("Trying to test if delete rpchar throws IllegalArgument when player has no RpChar");

        // Assign
        log.trace("Initialize Dto");
        DeletePlayerOrRpCharDto dto = new DeletePlayerOrRpCharDto("RandomId");

        log.trace("Initialize player object with above rpchar");
        Player player = Player.builder().discordID(dto.discordId()).build();

        log.trace("Initialize mock methods");
        when(mockPlayerRepository.findByDiscordID(dto.discordId())).thenReturn(Optional.of(player));

        // Act
        log.debug("Executing deleteRpChar");
        log.debug("Asserting that deleteRpChar throws IllegalArgumentException");
        var result = assertThrows(IllegalArgumentException.class, () -> playerService.deleteRpChar(dto));

        log.debug("Asserting that correct error message is thrown");
        assertThat(result.getMessage()).isEqualTo("No roleplay character found!");
    }
}
