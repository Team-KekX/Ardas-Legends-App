package com.ardaslegends.service;

import com.ardaslegends.domain.*;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.repository.player.PlayerRepository;
import com.ardaslegends.service.dto.player.*;
import com.ardaslegends.service.dto.player.rpchar.CreateRPCharDto;
import com.ardaslegends.service.dto.player.rpchar.UpdateRpCharDto;
import com.ardaslegends.service.exceptions.logic.player.PlayerServiceException;
import com.ardaslegends.service.exceptions.ServiceException;
import com.ardaslegends.service.external.MojangApiService;
import com.ardaslegends.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Responsible for the CRUD-Operations for the UserEntity
 */
@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class PlayerService extends AbstractService<Player, PlayerRepository> {

    private final PlayerRepository playerRepository;

    private final FactionService factionService;
    private final MojangApiService mojangApiService;

    private final DiscordApi api;

    private final BotProperties properties;

    public Page<Player> getPlayersPaginated(Pageable pageable) {
        var page = secureFind(pageable, playerRepository::findAll);
        return page;
    }

    @Transactional(readOnly = false)
    public Player createPlayer(CreatePlayerDto dto) {

        // Checking if input data is valid

        log.debug("Creating Player with Data: {}", dto);

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));
        // Executing queries for required data

        Faction queriedFaction = factionService.getFactionByName(dto.faction());

        UUIDConverterDto uuidConverterDto = mojangApiService.getUUIDByIgn(dto.ign());

        log.debug("Querying Player by IGN {}", dto.ign());
        var userQueriedByIgn = secureFind(dto.ign(), playerRepository::findPlayerByIgn);
        log.debug("Result of queryByIgn: {}", userQueriedByIgn.orElse(null));

        if (userQueriedByIgn.isPresent()) {
            log.warn("Player with same IGN has been found! Data {}, Player {}", dto, userQueriedByIgn.get());
            throw PlayerServiceException.ignAlreadyUsed(dto.ign());
        }

        log.debug("Querying Player by DiscordId {}", dto.discordID());
        var userQueriedByDiscordID = secureFind(dto.discordID(), playerRepository::findByDiscordID);

        if (userQueriedByDiscordID.isPresent()) {
            log.warn("Player with same IGN or DiscordId has been found! Data {}, Player {}", dto, userQueriedByDiscordID);
            throw PlayerServiceException.alreadyRegistered();
        }
        log.debug("No player with ign, {} and discordId, {} found in database", dto.ign(), dto.discordID());

        // Creating player and Saving into database

        log.debug("Trying to create and save entity");
        Player player = new Player(dto.ign(), uuidConverterDto.id(), dto.discordID(), queriedFaction);

        log.debug("Persisting player entity with ign {}, discordId {} into the database", dto.ign(), dto.discordID());
        player = secureSave(player, playerRepository);

        log.info("Created Player: {}", dto);
        return player;
    }

    @Transactional(readOnly = false)
    public RPChar createRoleplayCharacter(CreateRPCharDto dto) {

        // Checking if input data is valid

        log.debug("Creating Roleplay Character with Data {}", dto);

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields()).map(field -> field.getName()).collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields()).
                filter(field -> field.getType().
                        isAssignableFrom(String.class))
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        if(dto.title().length() > 25) {
            log.warn("CreateRPChar title is too long");
            throw new IllegalArgumentException("Title exceeds maximum length of 25 Characters [%s, Length %s]".formatted(dto.title(), dto.title().length()));
        }

        // Executing queries for required data

        log.debug("Fetching required Data..");

        val actualPlayer = getPlayerByDiscordId(dto.discordId());             // Throws IllegalArgument, NullPointer or ServiceException if it does not succeed
        log.debug("Fetched Player by DiscordId [{}]", actualPlayer);

        log.debug("Checking if the player has a faction");
        if(actualPlayer.getFaction() == null) {
            log.warn("Player [{}] is not in a faction - cannot create new RpChar!", actualPlayer);
            throw ServiceException.createRpCharNoFaction(actualPlayer.getIgn());
        }

        // Checking if the player already has an RPChar
        actualPlayer.getActiveCharacter().ifPresent(rpChar -> {
            log.warn("Player [{}] already has an RPChar [{}]", actualPlayer, rpChar.getName());
            throw new IllegalArgumentException("Player [%s] already has an RPChar [%s], delete the old one if you want a new Character!".formatted(actualPlayer, actualPlayer.getRpChars()));
        });

        Optional<Player> fetchedPlayer = secureFind(dto.rpCharName(), playerRepository::queryPlayerByRpChar);

        if (fetchedPlayer.isPresent()) {
            log.warn("Player found with same RPChar Name [{}], [{}]", actualPlayer, dto.rpCharName());
            throw new IllegalArgumentException("Roleplay Character with name [%s] already exists!".formatted(dto.rpCharName()));
        }
        log.debug("No RPChar with name [{}] found - continuing with creation", dto.rpCharName());

        // Saving RPChar to the Database

        log.debug("Creating RpChar instance");
        RPChar createdChar = new RPChar(actualPlayer, dto.rpCharName(), dto.title(), dto.gear(), dto.pvp(), null);
        log.debug("Trying to persist RPChar [{}]", createdChar);
        actualPlayer.addActiveRpChar(createdChar);

        val persistedPlayer = secureSave(actualPlayer, playerRepository);

        log.info("Created RPChar [{}]", createdChar);
        return persistedPlayer.getActiveCharacter()
                .orElseThrow(() -> PlayerServiceException.unexpectedErrorSavingCharacter(persistedPlayer.getIgn()));
    }

    public Player getPlayerByIgn(String ign) {
        log.debug("Fetching Player with Ign: {}", ign);
        Objects.requireNonNull(ign, "IGN must not be null!");

        Optional<Player> fetchedPlayer = secureFind(ign, playerRepository::findPlayerByIgn);

        if (fetchedPlayer.isEmpty()) {
            log.warn("No player with ign {} found!", ign);
            throw ServiceException.cannotReadEntityDueToNotExisting(Player.class.getSimpleName(), "ign", ign);
        }
        log.info("Successfully fetched player: {}", fetchedPlayer.get());
        return fetchedPlayer.get();
    }

    public Player getPlayerByDiscordId(String discordId) {
        log.debug("Fetching Player with Discord ID: {}", discordId);
        Objects.requireNonNull(discordId, "DiscordId must not be null!");

        Optional<Player> fetchedPlayer = secureFind(discordId, playerRepository::findByDiscordID);

        if (fetchedPlayer.isEmpty()) {
            log.warn("No player with discordId {} found!", discordId);
            throw PlayerServiceException.noPlayerFound(discordId);
        }
        log.info("Successfully fetched player: {}", fetchedPlayer.get());
        return fetchedPlayer.get();
    }

    @Transactional(readOnly = false)
    public Player updatePlayerFaction(UpdatePlayerFactionDto dto) {
        log.debug("Updating the Faction of player '{}' to '{}'", dto.discordId(), dto.factionName());

        /*
        Validating input
         */
        log.trace("Checking if input data is present...");
        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()));


        /*
        Finding the player instance by the ign
         */

        Player player = null;

        /*
        I don't have to put this into a try-catch block because the getPlayerByIgn function already
        throws all the needed exceptions
         */
        log.trace("Trying to search for player with ign '{}'", dto.discordId());
        player = getPlayerByDiscordId(dto.discordId());
        log.debug("Found player '{}'", dto.discordId());

        /*
        Finding the Faction instance by faction name
         */

         /*
        I don't have to put this into a try-catch block because the getPlayerByIgn function already
        throws all the needed exceptions
         */
        log.trace("Trying to search for Faction with name '{}'", dto.factionName());
        Faction faction = factionService.getFactionByName(dto.factionName());
        log.debug("Found Faction '{}'", dto.factionName());

        //Saving the old faction so I can log it later on
        Faction oldFaction = player.getFaction();

        /*
        Updating the Faction and saving the player
         */

        log.trace("Setting faction of player '{}' to '{}'", player.getIgn(), faction);
        player.setFaction(faction);

        log.debug("Saving the updated Player to database...");
        player = secureSave(player, playerRepository);

        log.info("Updated faction of player '{}' - From '{}' to '{}'", player.getIgn(), oldFaction.getName(), player.getFaction().getName());
        return player;
    }

    @Transactional(readOnly = false)
    public Player updateIgn(UpdatePlayerIgnDto dto) {
        log.debug("Updating ign of player [{}]", dto);

        // Checking if data is valid

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()));


        // Get the player who issued the command
        Player playerToUpdate = getPlayerByDiscordId(dto.discordId());

        // Check if somebody already has this IGN
        Optional<Player> playerByIgn = secureFind(dto.ign(), playerRepository::findPlayerByIgn);

        if(playerByIgn.isPresent()) {
            log.warn("Player with ign [{}] already existing!", dto.ign());
            throw new IllegalArgumentException("A player with the IGN [%s] [%s] already exists".formatted(dto.ign(), playerByIgn.get()));
        }
        log.debug("No player with ign [{}] existing yet - continuing", dto.ign());

        UUIDConverterDto uuidConverterDto = mojangApiService.getUUIDByIgn(dto.ign());
        log.debug("uuId to ign returned [{}]", dto);

        //Save old IGN for later logging
        String oldIgn = playerToUpdate.getIgn();

        // Mutate player Object
        playerToUpdate.setIgn(dto.ign());
        playerToUpdate.setUuid(uuidConverterDto.id());

        playerToUpdate = secureSave(playerToUpdate, playerRepository);

        log.info("Successfully updated IGN of player '{}' from '{}' to '{}'!", playerToUpdate.getDiscordID(), oldIgn, playerToUpdate.getIgn());
        return playerToUpdate;
    }

    @Transactional(readOnly = false)
    public Player updateDiscordId(UpdateDiscordIdDto dto) {
        log.debug("Updating discord id, data [{}]", dto);

        // Checking if data is valid

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()));

        // Get the player entity which is to be updated
        log.debug("Fetching old player");
        Player playerToUpdate = getPlayerByDiscordId(dto.oldDiscordId());

        // Check if a player is already registered with the new account
        log.debug("Fetching player with new to DiscordId to see if a player already exists");
        Optional<Player> fetchedPlayer = secureFind(dto.newDiscordId(), playerRepository::findByDiscordID);

        if(fetchedPlayer.isPresent()) {
            log.warn("Player found that is registered with the same discordId [{}] [{}]", dto.newDiscordId(), fetchedPlayer.get());
            throw new IllegalArgumentException("A Player with the new discordId already exists! [%s]".formatted(fetchedPlayer.get()));
        }

        log.debug("Update DiscordId of player");
        playerToUpdate.setDiscordID(dto.newDiscordId());

        log.debug("Trying to persist Player [{}]", playerToUpdate);
        playerToUpdate = secureSave(playerToUpdate, playerRepository);

        log.info("Successfully updated DiscordId of player '{}' from '{}' to '{}'!", playerToUpdate.getDiscordID(), dto.oldDiscordId(), playerToUpdate.getDiscordID());
        return playerToUpdate;
    }

    @Transactional(readOnly = false)
    public RPChar updateCharacterName(UpdateRpCharDto dto) {
        log.debug("Updating character name, data [{}]", dto);

        // Checking if data is valid

        ServiceUtils.checkNulls(dto, List.of("discordId", "charName"));

        ServiceUtils.checkBlanks(dto, List.of("discordId", "charName"));

        // Get the player entity which is to be updated
        log.debug("Fetching player");
        val playerToUpdate = getPlayerByDiscordId(dto.discordId());

        // Check if player actually has an rpchar and store that character
        val rpChar = playerToUpdate.getActiveCharacter().orElseThrow(() -> {
            log.warn("No Rpchar found at player [{}]", playerToUpdate);
            return new IllegalArgumentException("Player does not have a RPChar and therefore cannot update its name!");
        });

        // Check if a player has already taken the name
        log.debug("Fetching player with new RpChar name to see if that name is already taken");
        secureFind(dto.charName(), playerRepository::queryPlayerByRpChar).ifPresent(player -> {
            log.warn("Player found that has an Rpchar with the same name [{}] [{}]", dto.charName(), player);
            throw new IllegalArgumentException("RpChar Name by player [%s] is already taken!".formatted(player));
        });

        log.debug("Update RpChar Name");
        rpChar.setName(dto.charName());

        log.debug("Trying to persist player [{}]", playerToUpdate);
        val updatedPlayer = secureSave(playerToUpdate, playerRepository);

        val updatedCharacter = updatedPlayer.getActiveCharacter().orElseThrow(() -> PlayerServiceException.unexpectedErrorSavingCharacter(updatedPlayer.getIgn()));
        log.info("Successfully updated Rp Character Name of player [{}] to [{}]!", updatedPlayer, updatedCharacter.getName());
        return updatedCharacter;
    }

    @Transactional(readOnly = false)
    public RPChar updateCharacterTitle(UpdateRpCharDto dto) {
        log.debug("Updating character title to '{}' for player's ({}) rpchar '{}'", dto.title(), dto.discordId(), dto.charName());

        // Checking if data is valid
        log.debug("Validating DTO data");
        ServiceUtils.checkNulls(dto, List.of("discordId", "title"));
        ServiceUtils.checkBlanks(dto, List.of("discordId", "title"));

        if(dto.title().length() > 25) {
            log.warn("updateCharacterTitle - title is too long");
            throw new IllegalArgumentException("Title exceeds maximum length of 25 Characters [%s, Length %s]".formatted(dto.title(), dto.title().length()));
        }

        // Get the player entity which is to be updated
        log.debug("Fetching player");
        val playerToUpdate = getPlayerByDiscordId(dto.discordId());

        // Check if player actually has an rpchar
        val character = playerToUpdate.getActiveCharacter().orElseThrow(() -> {
            log.warn("No Rpchar found at player [{}]", playerToUpdate);
            return new IllegalArgumentException("Player does not have a RPChar and therefore cannot update its title!");
        });

        log.debug("Update RpChar Title");
        character.setTitle(dto.title());

        log.debug("Trying to persist player [{}]", playerToUpdate);
        val updatedPlayer = secureSave(playerToUpdate, playerRepository);

        val updatedChar = updatedPlayer.getActiveCharacter().orElseThrow(() -> PlayerServiceException.unexpectedErrorSavingCharacter(updatedPlayer.getIgn()));
        log.info("Successfully updated Rp Character title of player [{}] to [{}]!", updatedPlayer, updatedChar.getTitle());
        return updatedChar;
    }

    @Transactional(readOnly = false)
    public RPChar updateCharacterGear(UpdateRpCharDto dto) {
        log.debug("Updating character gear to '{}' for player's ({}) rpchar '{}'", dto.gear(), dto.discordId(), dto.charName());

        // Checking if data is valid
        log.debug("Validating DTO data");
        ServiceUtils.checkNulls(dto, List.of("discordId", "gear"));
        ServiceUtils.checkBlanks(dto, List.of("discordId", "gear"));

        // Get the player entity which is to be updated
        log.debug("Fetching player");
        val playerToUpdate = getPlayerByDiscordId(dto.discordId());

        // Check if player actually has an rpchar
        val character = playerToUpdate.getActiveCharacter().orElseThrow(() -> {
            log.warn("No Rpchar found at player [{}]", playerToUpdate);
            return new IllegalArgumentException("Player does not have a RPChar and therefore cannot update its gear!");
        });

        //Update the gear
        log.debug("Update RpChar Gear");
        character.setGear(dto.gear());

        log.debug("Trying to persist player [{}]", playerToUpdate);
        val updatedPLayer = secureSave(playerToUpdate, playerRepository);

        val updatedCharacter = updatedPLayer.getActiveCharacter().orElseThrow(() -> PlayerServiceException.unexpectedErrorSavingCharacter(updatedPLayer.getIgn()));
        log.info("Successfully updated Rp Character gear of player [{}] to [{}]!", updatedPLayer, updatedCharacter.getGear());
        return updatedCharacter;
    }


    @Transactional(readOnly = false)
    public RPChar updateCharacterPvp(UpdateRpCharDto dto) {
        log.debug("Updating character's pvp status to '{}' for player's ({}) rpchar '{}'", dto.pvp(), dto.discordId(), dto.charName());

        // Checking if data is valid
        log.debug("Validating DTO data");
        ServiceUtils.checkNulls(dto, List.of("discordId", "pvp"));
        ServiceUtils.checkBlanks(dto, List.of("discordId", "pvp"));

        // Get the player entity which is to be updated
        log.debug("Fetching player");
        val playerToUpdate = getPlayerByDiscordId(dto.discordId());

        // Check if player actually has an rpchar
        val character = playerToUpdate.getActiveCharacter().orElseThrow(() -> {
            log.warn("No Rpchar found at player [{}]", playerToUpdate);
            return new IllegalArgumentException("Player does not have a RPChar and therefore cannot update its pvp status!");
        });

        //Update the pvp status
        log.debug("Update RpChar PvP Status");
        character.setPvp(dto.pvp());

        log.debug("Trying to persist player [{}]", playerToUpdate);
        val updatedPlayer = secureSave(playerToUpdate, playerRepository);

        val updatedCharacter = updatedPlayer.getActiveCharacter().orElseThrow(() -> PlayerServiceException.unexpectedErrorSavingCharacter(updatedPlayer.getIgn()));
        log.info("Successfully updated Rp Character PvP Status of player [{}] to [{}]!", updatedPlayer, updatedCharacter.getPvp());
        return updatedCharacter;
    }

    @Transactional(readOnly = false)
    public Player deletePlayer(DiscordIdDto dto) {
        log.debug("Deleting player with data [{}]", dto);

        // Validating Data

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()));

        // Get player who issued the command
        Player player = getPlayerByDiscordId(dto.discordId());
        log.debug("Found player which is to be deleted [{}]", player);

        log.debug("Trying to delete player [{}]", player);
        secureDelete(player, playerRepository);

        log.info("Succesfully deleted player [{}]", player);
        return player;
    }

    @Transactional(readOnly = false)
    public RPChar deleteRpChar(DiscordIdDto dto) {
        log.debug("Deleting Rpchar with data [{}]", dto);

        // Validating Data

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()));

        // Get player who issued the command
        Player player = getPlayerByDiscordId(dto.discordId());
        log.debug("Found player which issued the command [{}]", player);

        val deletedCharacter = player.deleteCharacter();
        log.debug("Deleting RpChar [{}] from player [{}]", deletedCharacter, player);

        log.debug("Trying to save player with deleted RpChar [{}]", player);
        player = secureSave(player, playerRepository);

        log.info("Succesfully deleted rpchar [{}]", deletedCharacter);
        return deletedCharacter;
    }

    @Transactional(readOnly = false)
    public RPChar injureChar(DiscordIdDto dto) {
        log.debug("Trying to injure character of player [{}]", dto.discordId());

        log.trace("Fetching player instance of player [{}]", dto.discordId());
        val player = getPlayerByDiscordId(dto.discordId());
        log.trace("Found player [{}]", player);

        log.debug("Checking if player [{}] has an rpchar", player);
        val rpChar = player.getActiveCharacter().orElseThrow(() -> {
            log.warn("Player [{}] does not have a roleplay character!", player);
            return PlayerServiceException.noRpChar();
        });

        log.debug("Player [{}] has RpChar [{}]", player, rpChar);

        log.debug("Setting injured = true for character [{}]", rpChar);
        log.debug("Unbinding player from army [{}]", rpChar.getBoundTo());
        rpChar.injure();

        log.debug("Persisting player");
        val updatedPlayer = secureSave(player, playerRepository);

        log.info("Successfully injured character [{}] of player [{}]", rpChar, updatedPlayer);
        return rpChar;
    }

    @Transactional(readOnly = false)
    public RPChar healStart(DiscordIdDto dto) {
        log.debug("Trying to start healing character of player [{}]", dto.discordId());

        log.trace("Fetching player instance of player [{}]", dto.discordId());
        val player = getPlayerByDiscordId(dto.discordId());
        log.trace("Found player [{}]", player);

        log.debug("Checking if player has a character");
        val rpchar = player.getActiveCharacter().orElseThrow(() -> {
            log.warn("Player [{}] has no roleplay character and therefore cannot heal it!", player);
            return PlayerServiceException.noRpChar();
        });

        log.debug("Player [{}] has an rpchar called [{}]", player , rpchar);

        log.debug("Checking if the character is injured");
        if(!rpchar.getInjured()) {
            log.warn("Character [{}] of player [{}] is not injured and therefore cannot be healed!", rpchar, player);
            throw PlayerServiceException.cannotHealNotInjured(rpchar.getName());
        }
        log.debug("Character is injured - can heal");

        log.debug("Checking if there is a claimbuild with House of Healing in current region [{}] of character [{}]", rpchar.getCurrentRegion(), rpchar);
        Set<ClaimBuild> claimbuilds = rpchar.getCurrentRegion().getClaimBuilds();
        log.trace("Claimbuilds in region [{}]: [{}]", rpchar.getCurrentRegion(), claimbuilds);

        val cbWithHoH = claimbuilds.stream().filter(claimBuild -> claimBuild.getSpecialBuildings().contains(SpecialBuilding.HOUSE_OF_HEALING))
                .findFirst().orElseThrow(() -> {
                    log.warn("Region [{}] has no claimbuilds with House of Healing!", rpchar.getCurrentRegion());
                    return PlayerServiceException.cannotHealNoCbWithHoH(rpchar.getName(), rpchar.getCurrentRegion().getId(), claimbuilds.toString());
                });
        log.trace("Region [{}] has claimbuild with House of Healing: [{}]", rpchar.getCurrentRegion(), cbWithHoH.getName());

        log.debug("Setting isHealing");
        rpchar.startHealing();

        log.debug("Persisting player");
        val updatedPlayer = secureSave(player, playerRepository);

        log.info("Successfully started healing character [{}] of player [{}]!", rpchar, updatedPlayer);
        return rpchar;
    }

    @Transactional(readOnly = false)
    public RPChar healStop(DiscordIdDto dto) {
        log.debug("Trying to stop healing character of player [{}]", dto.discordId());

        log.trace("Fetching the player instance");
        val player = getPlayerByDiscordId(dto.discordId());
        log.trace("Found player [{}]", player);

        log.debug("Checking if player has a character");
        val rpchar = player.getActiveCharacter().orElseThrow(() -> {
            log.warn("Player [{}] has no roleplay character and therefore cannot heal it!", player);
            return PlayerServiceException.noRpChar();
        });
        log.debug("Player [{}] has an rpchar called [{}]", player , rpchar);

        log.debug("Checking if rpchar is healing. Current healing flag: [{}]", rpchar.getIsHealing());
        if(!rpchar.getIsHealing()) {
            log.warn("Character [{}] of player [{}] is not currently healing and therefore cannot stop healing!", rpchar, player);
            throw PlayerServiceException.cannotStopHealBecauseCharNotHealing(rpchar.getName());
        }
        log.debug("Char is healing - continuing");

        log.debug("Setting isHealing to false");
        rpchar.setIsHealing(false);
        rpchar.setStartedHeal(null);
        rpchar.setHealEnds(null);

        log.debug("Persisting player");
        val updatedPlayer = secureSave(player, playerRepository);

        log.info("Successfully stopped healing character [{}] of player [{}]", rpchar, updatedPlayer);
        return rpchar;
    }

    public List<Player> savePlayers(List<Player> players) {
        log.debug("Saving players [{}]", players);
        return secureSaveAll(players, playerRepository);
    }

    public boolean checkIsStaff(String discordId) {
        Objects.requireNonNull(discordId);

        val user = api.getUserById(discordId).join();
        val staffRoles = properties.getDiscordStaffRoles();

        return user.getRoles(properties.getDiscordServer())
                .stream()
                .anyMatch(staffRoles::contains);
    }
}
