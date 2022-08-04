package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.service.dto.army.*;
import com.ardaslegends.data.service.dto.unit.UnitTypeDto;
import com.ardaslegends.data.service.exceptions.army.ArmyServiceException;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.service.exceptions.claimbuild.ClaimBuildServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ArmyService extends AbstractService<Army, ArmyRepository> {
    private final ArmyRepository armyRepository;
    private final MovementRepository movementRepository;
    private final PlayerService playerService;
    private final FactionRepository factionRepository;
    private final UnitTypeService unitTypeService;
    private final ClaimBuildRepository claimBuildRepository;

    // TODO: Need to test
    public Army createArmy(CreateArmyDto dto) {
        log.debug("Creating army with data [{}]", dto);

        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);
        Arrays.stream(dto.units()).forEach(ServiceUtils::checkAllBlanks);

        log.debug("Fetching required Data");

        log.trace("Fetching if an army with name [{}] already exists.", dto.name());
        Optional<Army> fetchedArmy = secureFind(dto.name(), armyRepository::findById);

        if(fetchedArmy.isPresent()) {
            log.warn("Army with name [{}] already exists", dto.name());
            throw new IllegalArgumentException("Army with name %s already exists, please choose a different name".formatted(dto.name()));
        }


        log.trace("Fetching Executor by discordId [{}]", dto.executorDiscordId());
        Player fetchedPlayer = playerService.getPlayerByDiscordId(dto.executorDiscordId());

        log.debug("Assembling Units Map, fetching units");
        var unitTypes = Arrays.stream(dto.units())
                .collect(Collectors.toMap(
                        unitTypeDto -> unitTypeService.getUnitTypeByName(unitTypeDto.unitTypeName()),
                        UnitTypeDto::amount
                ));

        log.trace("Fetching Claimbuild with name [{}]", dto.claimBuildName());
        Optional<ClaimBuild> fetchedClaimbuild = secureFind(dto.claimBuildName(), claimBuildRepository::findById);

        if(fetchedClaimbuild.isEmpty()) {
            log.warn("No ClaimBuild found with name [{}]", dto.claimBuildName());
            throw ClaimBuildServiceException.noCbWithName(dto.claimBuildName());
        }

        ClaimBuild inputClaimBuild = fetchedClaimbuild.get();

        log.trace("Finished fetching required data");

        log.debug("Checking if the Player has the same faction as the Claimbuild");
        if(!inputClaimBuild.getOwnedBy().equals(fetchedPlayer.getFaction())) {
            log.warn("Player [{}] and Claimbuild [{}] not in the same faction!");
            throw ArmyServiceException.cannotCreateArmyFromClaimbuildInDifferentFaction(fetchedPlayer.getFaction().getName(), inputClaimBuild.getOwnedBy().getName());
        }

        log.debug("Checking if ClaimBuild can create another army");

        if(inputClaimBuild.getCreatedArmies().size() >= inputClaimBuild.getType().getMaxArmies()) {
            log.warn("ClaimBuild [{}] already has max amount of armies created! Armies {}",inputClaimBuild ,inputClaimBuild.getCreatedArmies() );
            throw ArmyServiceException.maxArmyOrCompany(inputClaimBuild.toString(), inputClaimBuild.getCreatedArmies().toString());
        }

        log.debug("Checking if token count does not exceed 30");
        // Calculate the tokens that were used
        log.trace("Calculating amount of tokens used");
        int tokenCount = 0;
        List<Unit> units = new ArrayList<>();
        for (UnitType unit: unitTypes.keySet()) {
            // Get the UnitType cost and multiply it by the count of that unit, which is stored in the units Map
            tokenCount += unit.getTokenCost() * unitTypes.get(unit);
            units.add(new Unit(null, unit, null, unitTypes.get(unit),unitTypes.get(unit)));
        }

        log.debug("Calculated Token count is [{}]", tokenCount);
        if(tokenCount > 30) {
            log.warn("Token count exceeds 30 [{}]", tokenCount);
            throw ArmyServiceException.tooHighTokenCount(tokenCount);
        }

        // TODO: Relay info if army is free or costs something

        log.trace("Assembling Army Object");
        Army army = new Army(dto.name(),
                dto.armyType(),
                fetchedPlayer.getFaction(),
                inputClaimBuild.getRegion(),
                null,
                null,
                null,
                inputClaimBuild,
                30-tokenCount,
                false,
                inputClaimBuild);

        log.trace("Adding the army to each unit");
        Army finalArmy = army;
        units.forEach(unit -> unit.setArmy(finalArmy));

        army.setUnits(units);

        log.debug("Trying to persist the army object");
        army = secureSave(army, armyRepository);

        log.info("Finished CreateArmy!");
        return army;
    }

    @Transactional(readOnly = false)
    public Army healStart(UpdateArmyDto dto) {
        log.debug("Trying to start healing for army [{}]", dto.armyName());

        ServiceUtils.checkNulls(dto, List.of("executorDiscordId", "armyName"));
        ServiceUtils.checkBlanks(dto, List.of("executorDiscordId", "armyName"));

        log.debug("Fetching required data");

        log.trace("Fetching Player");
        Player player = playerService.getPlayerByDiscordId(dto.executorDiscordId());

        log.trace("Fetching Army");
        Army army = getArmyByName(dto.armyName());

        log.debug("Checking if army and player are in the same faction");
        if(!player.getFaction().equals(army.getFaction())) {
            log.warn("Player [{}:{}]and Army [{}:{}] are not in the same faction ", player, player.getFaction(), army, army.getFaction());
            throw ArmyServiceException.armyAndPlayerInDifferentFaction(player.getFaction().toString(), army.getFaction().toString());
        }

        log.debug("Checking if army is stationed at a CB");
        // Army is null or Claimbuild does not have a House of healing
        if(army.getStationedAt() == null || !army.getStationedAt().getSpecialBuildings().contains(SpecialBuilding.HOUSE_OF_HEALING)) {
            log.warn("Army [{}] is not stationed at a claimbuild");
            throw ArmyServiceException.needToStationArmyAtCbWithHouseOfHealing(army.toString());
        }

        log.debug("All checks validated, persisting now");

        army.setHealing(true);
        army = secureSave(army, armyRepository);

        log.info("Army [{}] is now healing", army.toString());
        return army;
    }

    @Transactional(readOnly = false)
    public Army healStop(UpdateArmyDto dto) {
        log.debug("Trying to start healing for army [{}]", dto.armyName());

        ServiceUtils.checkNulls(dto, List.of("executorDiscordId", "armyName"));
        ServiceUtils.checkBlanks(dto, List.of("executorDiscordId", "armyName"));

        log.trace("Fetching Player");
        Player player = playerService.getPlayerByDiscordId(dto.executorDiscordId());

        log.trace("Fetching Army");
        Army army = getArmyByName(dto.armyName());

        log.debug("Checking if army is healing");
        if(!army.isHealing()) {
            log.warn("Army [{}] is not healing, cant stop it!");
            throw ArmyServiceException.armyIsNotHealing(army.toString());
        }

        log.debug("Checking if army and player are in the same faction");
        if(!player.getFaction().equals(army.getFaction())) {
            log.warn("Player [{}:{}]and Army [{}:{}] are not in the same faction ", player, player.getFaction(), army, army.getFaction());
            throw ArmyServiceException.armyAndPlayerInDifferentFaction(player.getFaction().toString(), army.getFaction().toString());
        }

        army.setHealing(false);
        army = secureSave(army, armyRepository);

        log.info("Army [{}] now stopped healing", army.toString());
        return army;
    }

    @Transactional(readOnly = false)
    public Army bind(BindArmyDto dto) { //TODO Change to Army.bind()
        log.debug("Binding army [{}] to player with discord id [{}]", dto.armyName(), dto.targetDiscordId());

        log.trace("Validating data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Getting the executor player's instance");
        Player executor = playerService.getPlayerByDiscordId(dto.executorDiscordId());

        /*
        Checking if the executor is the faction leader - if not, throw error that player doesn't have permission to bind
         */
        boolean isBindingSelf = dto.executorDiscordId().equals(dto.targetDiscordId()); //Says if the player is binding themselves

        log.debug("Checking if executor and target are not equal");
        if (!isBindingSelf) {
            //TODO Check for lords as well
            log.trace("Executor and target are not equal - checking if executor is faction leader");
            if (!executor.equals(executor.getFaction().getLeader())) {
                log.warn("Executor player [{}] is not faction leader of faction [{}]!", executor, executor.getFaction());
                throw ArmyServiceException.notFactionLeader(executor.getFaction().getName());
            }
        }

        /*
        Setting target player
        If not binding self then fetch the target player from DB
         */
        log.trace("Getting the target player's instance");
        Player targetPlayer = null;
        if (isBindingSelf)
            targetPlayer = executor;
        else
            targetPlayer = playerService.getPlayerByDiscordId(dto.targetDiscordId());

        Army army = getArmyByName(dto.armyName());

        // TODO: Check for Wanderer or Allied Faction
        log.debug("Checking if army and player are in the same faction");
        if (!army.getFaction().equals(targetPlayer.getFaction())) {
            log.debug("Player and army are not in the same faction");
            log.debug("Checking if the faction is allied to the players faction");
            //TODO Check for allied faction
            log.debug("Checking if the player is a wanderer");
            if(targetPlayer.getFaction().getName().equals("Wanderer")) {
                log.debug("Target player is a wanderer - checking if executor is faction leader");
                //TODO Check for Lords as well
                if(!executor.equals(executor.getFaction().getLeader())) {
                    log.warn("Player [{}] is not faction leader of [{}] and therefore cannot bind wanderers!", executor, executor.getFaction());
                    throw ArmyServiceException.onlyLeaderCanBindWanderer();
                }
            }
            else {
                log.warn("Army [{}] and player [{}] are not in the same faction (army: [{}], player: [{}])", army.getName(), targetPlayer, army.getFaction(), targetPlayer.getFaction());
                throw ArmyServiceException.notSameFaction(army.getName(), targetPlayer.getFaction().getName(), army.getFaction().getName());
            }
        }

        log.debug("Checking if army and player are in the same region");
        if (!army.getCurrentRegion().equals(targetPlayer.getRpChar().getCurrentRegion())) {
            log.warn("Army and player are not in the same region!");
            throw ArmyServiceException.notInSameRegion(army.getName(), targetPlayer.getRpChar().getName());
        }

        log.debug("Checking if army is already bound to player");
        if (army.getBoundTo() != null) {
            log.warn("Army [{}] is already bound to another player [{}]!", army.getName(), army.getBoundTo());
            throw ArmyServiceException.alreadyBound(army.getName(), army.getBoundTo().getIgn());
        }

        log.debug("Checking if army is in an active movement");
        Optional<Movement> armyActiveMove = movementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army);
        if(armyActiveMove.isPresent()) {
            String destinationRegion =  armyActiveMove.get().getPath().getDestination();
            log.warn("Army [{}] is currently moving to region [{}] and therefore cannot be bound to player [{}]!", army.getName(), destinationRegion, targetPlayer);
            throw ArmyServiceException.cannotBindArmyIsMoving(army.getName(), destinationRegion);
        }

        log.debug("Checking if rp char is in an active movement");
        Optional<Movement> charActiveMove = movementRepository.findMovementByPlayerAndIsCurrentlyActiveTrue(targetPlayer);
        if(charActiveMove.isPresent()) {
            String destinationRegion =  charActiveMove.get().getPath().getDestination();
            log.warn("Character [{}] is currently moving to region [{}] and therefore cannot be bound to army [{}]!", targetPlayer, destinationRegion, army.getName());
            throw ArmyServiceException.cannotBindCharIsMoving(army.getName(), destinationRegion);
        }

        log.debug("Binding army [{}] to player [{}]...", army.getName(), targetPlayer);
        army.setBoundTo(targetPlayer);

        log.debug("Persisting newly changed army...");
        army = secureSave(army, armyRepository);

        log.info("Bound {} [{}] to player [{}]!", army.getArmyType().name(), army.getName(), targetPlayer);
        return army;
    }

    @Transactional(readOnly = false)
    public Army unbind(BindArmyDto dto) {
        log.debug("Unbinding army [{}] from player [{}] - executed by player [{}]", dto.armyName(), dto.targetDiscordId(), dto.executorDiscordId());

        log.trace("Validating data...");
        ServiceUtils.checkNulls(dto, List.of("armyName", "executorDiscordId"));
        ServiceUtils.checkBlanks(dto, List.of("armyName", "executorDiscordId"));

        log.trace("Calling playerService to get executor's instance");
        Player executor = playerService.getPlayerByDiscordId(dto.executorDiscordId());

        /*
        Getting the army and checking if the army has a player bound to it
         */
        log.debug("Getting the army object");

        log.trace("Fetching the army by name");
        Army army = getArmyByName(dto.armyName());

        log.debug("Getting the currently bound player");

        Player boundPlayer = army.getBoundTo();
        log.trace("Checking if the army has a player bound to it");
        if(boundPlayer == null) {
            log.warn("There is no player bound to the army [{}]", army);
            throw ArmyServiceException.noPlayerBoundToArmy(army.getName());
        }

        /*
        Checking if executor has permission to unbind player
        If target player is not the executor - check if executor is leader or lord
         */

        log.debug("Checking if executor has permission to unbind target player");

        log.trace("Checking if player is unbinding themselves");
        boolean isUnbindingSelf = executor.getDiscordID().equals(dto.targetDiscordId());

        if(!isUnbindingSelf) {
            log.debug("Executor is unbinding another player - checking if executor has permission to do so");
            //TODO check for lord as well
            if(!executor.equals(executor.getFaction().getLeader())) { //checking if executor is leader
                log.warn("Executor player [{}] is not faction leader or lord of [{}] and therefore cannot unbind other players!", executor, executor.getFaction());
                throw ArmyServiceException.notFactionLeader(executor.getFaction().getName());
            }
        }

        /*
        Unbind and save player
         */

        log.debug("Unbinding player and saving changes...");

        log.trace("Setting bound player to null");
        army.setBoundTo(null);
        log.trace("Persisting army");
        army = secureSave(army, armyRepository);

        log.info("Unbound player [{}] from army [{}] (faction [{}])", boundPlayer, army, army.getFaction());
        return army;
    }

    @Transactional(readOnly = false)
    public Army disband(DeleteArmyDto dto, boolean forced) {
        log.debug("Trying to disband army [{}] executed by player [{}]", dto.armyName(), dto.executorDiscordId());

        log.trace("Validating data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Getting the army instance");
        Army army = getArmyByName(dto.armyName());

        log.trace("Getting the player instance");
        Player player = playerService.getPlayerByDiscordId(dto.executorDiscordId());

        log.debug("Checking if the faction of player and army are same");
        if(!forced && !player.getFaction().equals(army.getFaction())) {
            log.warn("disbandArmy: Player [{}] and army [{}] do not share the same faction ([{}] and [{}])", player, army, player.getFaction(), army.getFaction());
            throw ArmyServiceException.notAllowedToDisbandNotSameFaction(army.getName(), army.getFaction().getName());
        }

        Faction faction = player.getFaction();

        boolean isAllowed = forced; //if it's a forced delete, instantly get permission

        log.debug("Checking if executor is faction leader");
        if(player.equals(faction.getLeader())) {
            log.debug("Player is faction leader - allowed to disband army");
            isAllowed = true;
        }

        //TODO: Check for lord as well

        if(!isAllowed) {
            log.warn("disbandArmy: Player [{}] is neither faction leader or lord of [{}] and therefore cannot disband army [{}]!", player, faction, army);
            throw ArmyServiceException.notAllowedToDisband();
        }

        log.debug("Deleting army [{}]", army);
        secureDelete(army, armyRepository);

        log.info("Disbanded army [{}] - executed by player [{}]", army, player);
        return army;
    }

    @Transactional(readOnly = false)
    public Army setFreeArmyTokens(UpdateArmyDto dto) {
        log.debug("Trying to update the free tokens of army [{}] to value [{}]", dto.armyName(), dto.freeTokens());

        log.trace("Validating data");
        ServiceUtils.checkNulls(dto, List.of("freeTokens", "armyName"));
        ServiceUtils.checkBlanks(dto, List.of("armyName"));

        log.trace("Getting the army by name [{}]", dto.armyName());
        Army army = getArmyByName(dto.armyName());
        int oldTokens = army.getFreeTokens();

        if(dto.freeTokens() < 0) {
            log.warn("Tried to set tokens of army [{}] to [{}] - value has to be positive", army, dto.freeTokens());
            throw ArmyServiceException.tokenNegative(dto.freeTokens());
        }

        if(dto.freeTokens() > 30) {
            log.warn("Tried to set tokens of army [{}] to [{}] - value has to be max. 30", army, dto.freeTokens());
            throw ArmyServiceException.tokenAbove30(dto.freeTokens());
        }

        log.debug("Setting the tokens of the army to [{}]", dto.freeTokens());
        army.setFreeTokens(dto.freeTokens());

        log.debug("Persisting army");
        army = armyRepository.save(army);

        log.info("Set tokens of army [{}] from [{}] to [{}]", army, oldTokens, army.getFreeTokens());
        return army;
    }

    @Transactional(readOnly = false)
    public Army pickSiege(PickSiegeDto dto) {
        log.debug("Trying to pick siege [{}] for army [{}] from cb [{}] - executed by player [{}]", dto.siege(), dto.armyName(), dto.claimbuildName(), dto.executorDiscordId());

        log.trace("Validating data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Getting army by name");
        Army army = getArmyByName(dto.armyName());

        log.debug("Checking if army is an army and not a company");
        if(!army.getArmyType().equals(ArmyType.ARMY)) {
            log.warn("Tried to pick siege for [{}], which is a [{}]!", army.getName(), army.getArmyType().name());
            throw ArmyServiceException.siegeOnlyArmyCanPick(army.getName());
        }

        log.trace("Getting player instance");
        Player player = playerService.getPlayerByDiscordId(dto.executorDiscordId());

        boolean isAllowed = false;
        log.debug("Checking if player is bound to army");
        if(army.getBoundTo() != null && army.getBoundTo().equals(player)) {
            log.debug("Player is bound to army - has permission to pick siege");
            isAllowed = true;
        }

        log.debug("Checking if player is faction leader");
        if(!isAllowed && player.equals(army.getFaction().getLeader())) {
            log.debug("Player is faction leader of [{}] and therefore has permission to pick siege without being bound", army.getFaction());
            isAllowed = true;
        }

        //TODO: Check for lords as well

        if(!isAllowed) {
            log.warn("Player is not bound to army and is not faction leader/lord of [{}]!", army.getFaction());
            throw ArmyServiceException.siegeNotFactionLeaderOrLord(army.getFaction().getName(), army.getName());
        }

        log.debug("Fetching claimbuild [{}]", dto.claimbuildName());
        Optional<ClaimBuild> foundCb = secureFind(dto.claimbuildName(), claimBuildRepository::findById);
        if(foundCb.isEmpty()) {
            log.warn("Found no claimbuild with name [{}]", dto.claimbuildName());
            throw ClaimBuildServiceException.noCbWithName(dto.claimbuildName());
        }

        ClaimBuild cb = foundCb.get();

        log.debug("Checking if army is in the same region as cb");
        if(!army.getCurrentRegion().equals(cb.getRegion())) {
            log.warn("Army [{}] is not in the same region as cb [{}] (Army's region: [{}], CB's region: [{}])", army.getName(), cb.getName(), army.getCurrentRegion().getId(), cb.getRegion().getId());
            throw ArmyServiceException.siegeArmyNotInSameRegionAsCB(army.getName(), army.getCurrentRegion().getId(), cb.getName(), cb.getRegion().getId());
        }

        log.debug("Checking if cb is owned by player's faction or an allied faction");
        if(!player.getFaction().equals(cb.getOwnedBy()) && !player.getFaction().getAllies().contains(cb.getOwnedBy())) {
            log.warn("CB [{}] is owned by Faction [{}] which is not allied to player [{}'s] Faction [{}]", cb.getName(), cb.getOwnedBy(), player.getIgn(), player.getFaction());
            throw ClaimBuildServiceException.differentFactionNotAllied(cb.getName(), cb.getOwnedBy().getName());
        }

        log.debug("Checking if inputted siege is available in CB");
        String inputtedSiege = dto.siege().toLowerCase();
        String availableSiege = cb.getSiege().toLowerCase();

        if(!availableSiege.contains(inputtedSiege)) {
            log.warn("Inputted siege [{}] is not available in cb's sieges [{}]", inputtedSiege, availableSiege);
            throw ArmyServiceException.siegeNotAvailable(dto.siege(), cb.getName(), cb.getSiege());
        }

        log.debug("Adding inputted siege to army siege");
        army.getSieges().add(dto.siege());

        log.trace("Persisting army");
        army = secureSave(army, armyRepository);

        log.info("Picked up siege [{}] for army [{}]!", dto.siege(), army);
        return army;
    }
    public Army getArmyByName(String armyName) {
        log.debug("Getting army by name [{}]", armyName);
        log.trace("Checking for null");
        Objects.requireNonNull(armyName);

        log.trace("Fetching the army by name");
        Optional<Army> fetchedArmy = secureFind(armyName, armyRepository::findArmyByName);

        log.trace("Checking if the army exists");
        if(fetchedArmy.isEmpty()) {
            log.warn("No army with the name [{}] found!",armyName);
            throw ArmyServiceException.noArmyWithName(armyName);
        }
        Army army = fetchedArmy.get();
        log.debug("Found army [{}] - type: [{}]", army.getName(), army.getArmyType().name());
        return army;
    }
}
