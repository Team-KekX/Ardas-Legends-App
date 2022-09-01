package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.service.dto.claimbuilds.DeleteClaimbuildDto;
import com.ardaslegends.data.repository.ProductionSiteRepository;
import com.ardaslegends.data.service.dto.claimbuilds.UpdateClaimbuildOwnerDto;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.dto.claimbuild.CreateClaimBuildDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.exceptions.claimbuild.ClaimBuildServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;

@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class ClaimBuildService extends AbstractService<ClaimBuild, ClaimBuildRepository> {

    private final ClaimBuildRepository claimbuildRepository;
    private final RegionRepository regionRepository;
    private final ProductionSiteRepository productionSiteRepository;

    private final FactionService factionService;
    private final PlayerService playerService;

    @Transactional(readOnly = false)
    public ClaimBuild setOwnerFaction(UpdateClaimbuildOwnerDto dto) {
        log.debug("Trying to set the controlling faction of Claimbuild [{}] to [{}]", dto.claimbuildName(), dto.newFaction());

        ServiceUtils.checkNulls(dto, List.of("claimbuildName", "newFaction"));
        ServiceUtils.checkBlanks(dto, List.of("claimbuildName", "newFaction"));

        log.trace("Fechting claimbuild with name [{}]", dto.claimbuildName());
        ClaimBuild claimBuild = getClaimBuildByName(dto.claimbuildName());

        log.trace("Fetching faction with name [{}]", dto.newFaction());
        Faction faction = factionService.getFactionByName(dto.newFaction());

        log.trace("Setting ownedBy");
        claimBuild.setOwnedBy(faction);

        log.debug("Persisting claimbuild [{}], with owning faction [{}]", claimBuild.getName(), claimBuild.getOwnedBy());
        claimBuild = secureSave(claimBuild, claimbuildRepository);

        log.info("Successfully returning claimbuild [{}] with new controlling faction [{}]", claimBuild.getName(), claimBuild.getOwnedBy());
        return claimBuild;
    }

    @Transactional(readOnly = false)
    public ClaimBuild createClaimbuild(CreateClaimBuildDto dto, boolean isNewlyCreated) {
        log.debug("Trying to create claimbuild with data [{}]", dto);

        log.trace("Validating data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Checking if claimbuild with name [{}] already exists", dto.name());
        log.trace("Fetching claimbuild with name [{}]", dto.name());
        Optional<ClaimBuild> existingClaimbuild = secureFind(dto.name(), claimbuildRepository::findById);
        log.trace("Checking if a claimbuild was found");
        if (existingClaimbuild.isPresent() && isNewlyCreated) {
            var claimbuild = existingClaimbuild.get();
            log.warn("Cannot create new claimbuild with name [{}] because another one  with same name already exists (region [{}] - owned by [{}])", claimbuild.getName(), claimbuild.getRegion(), claimbuild.getOwnedBy());
            throw ClaimBuildServiceException.cbAlreadyExists(claimbuild.getName(), claimbuild.getRegion().getId(), claimbuild.getOwnedBy().getName());
        }
        else if(existingClaimbuild.isEmpty() && !isNewlyCreated) {
            log.warn("Tried to updated claimbuild with name [{}] but no claimbuild was found!", dto.name());
            throw ClaimBuildServiceException.couldNotUpdateClaimbuildBecauseNotFound(dto.name());
        }

        log.debug("Getting the inputted region");
        log.trace("Fetching the region");
        Optional<Region> fetchedRegion = secureFind(dto.regionId(), regionRepository::findById);
        log.trace("Checking if region exists");
        if (fetchedRegion.isEmpty()) {
            log.warn("Region with id [{}] does not exist!", dto.regionId());
            throw ServiceException.regionDoesNotExist(dto.regionId());
        }
        Region region = fetchedRegion.get();
        log.trace("Successfully found region [{}]", dto.regionId());

        log.debug("Getting the inputted faction");
        log.trace("Fetching the faction");
        Faction faction = factionService.getFactionByName(dto.faction());

        log.debug("Getting the Claimbuild Type");
        ClaimBuildType type = null;
        try {
            log.trace("Trying to get enum value of inputted type [{}]", dto.type());
            type = ClaimBuildType.valueOf(dto.type().replace(' ', '_').toUpperCase());
        }
        catch (Exception e) {
            log.warn("Could not find claimbuild type [{}]!", dto.type());
            throw ClaimBuildServiceException.noCbTypeFound(dto.type());
        }

        log.trace("Checking if type is [{}]", ClaimBuildType.CAPITAL);
        if(type.equals(ClaimBuildType.CAPITAL) && isNewlyCreated) {
            log.debug("CB Type is [{}] - Checking if faction already has a [{}]", ClaimBuildType.CAPITAL, ClaimBuildType.CAPITAL);
            boolean hasCapital = faction.getClaimBuilds().stream().anyMatch(cb -> cb.getType().equals(ClaimBuildType.CAPITAL));
            log.trace("Faction has capital: [{}]", hasCapital);
            if(hasCapital) {
                log.warn("Faction [{}] already has a claimbuild of type [{}]", faction, ClaimBuildType.CAPITAL);
                throw ClaimBuildServiceException.factionAlreadyHasCapital(faction.getName());
            }
        }

        log.debug("Building Claimbuild coordinates");
        Coordinate coordinate = new Coordinate(dto.xCoord(), dto.yCoord(), dto.zCoord());

        log.trace("Calling createBuiltByFromString");
        Set<Player> builtBy = createBuiltByFromString(dto.builtBy());

        log.trace("Calling createSpecialBuildingsFromString");
        List<SpecialBuilding> specialBuildings = createSpecialBuildingsFromString(dto.specialBuildings());

        log.debug("Creating the claimbuild instance so we can instantiate production sites and special buildings");
        //production sites will be set later on because they need the claimbuild instance when getting created
        ClaimBuild claimBuild = null;
        if(isNewlyCreated) {
            claimBuild = new ClaimBuild(dto.name(), region, type, faction, coordinate, new ArrayList<>(), new ArrayList<>(), null,
                    specialBuildings, dto.traders(), dto.siege(), dto.numberOfHouses(), builtBy, type.getFreeArmies(), type.getFreeTradingCompanies());
        }
        else {
            claimBuild = existingClaimbuild.get();
            claimBuild.setRegion(region);
            claimBuild.setType(type);
            claimBuild.setOwnedBy(faction);
            claimBuild.setCoordinates(coordinate);
            claimBuild.setSpecialBuildings(specialBuildings);
            claimBuild.setTraders(dto.traders());
            claimBuild.setSiege(dto.siege());
            claimBuild.setNumberOfHouses(dto.numberOfHouses());
            claimBuild.setBuiltBy(builtBy);

            log.debug("Setting the region [{}] to be claimed by [{}]", region.getId(), faction.getName());
            region.addFactionToClaimedBy(faction);
        }

        log.trace("Calling createProductionSitesFromString");
        List<ProductionClaimbuild> prodSites = createProductionSitesFromString(dto.productionSites(), claimBuild);

        log.trace("Setting production sites");
        claimBuild.setProductionSites(prodSites);

        String logStr = isNewlyCreated ? "newly created" : "updated";

        log.debug("Persisting the {} Claimbuild", logStr);
        claimBuild = secureSave(claimBuild, claimbuildRepository);

        logStr = isNewlyCreated ? "created new" : "updated";
        log.info("Successfully {} [{}] claimbuild [{}] for faction [{}] in region [{}]",logStr, type, claimBuild.getName(), faction, region.getId());
        return claimBuild;
    }
    @Transactional(readOnly = false)
    public ClaimBuild deleteClaimbuild(DeleteClaimbuildDto dto) {
        log.debug("Trying to delete claimbuild with name [{}]", dto.claimbuildName());

        ServiceUtils.checkNulls(dto, List.of("claimbuildName"));
        ServiceUtils.checkBlanks(dto, List.of("claimbuildName"));

        ClaimBuild claimBuild = getClaimBuildByName(dto.claimbuildName());

        secureDelete(claimBuild, claimbuildRepository);
        log.trace("Stationed Armies [{}]", claimBuild.getStationedArmies());
        log.trace("Created Armies [{}]", claimBuild.getCreatedArmies());
        return claimBuild;
    }

    public ClaimBuild getClaimBuildByName(String name) {
        log.debug("Getting Claimbuild with name [{}]", name);

        Objects.requireNonNull(name, "Name must not be null");
        ServiceUtils.checkBlankString(name, "Name");

        log.debug("Fetching unit with name [{}]", name);
        Optional<ClaimBuild> fetchedBuild = secureFind(name, claimbuildRepository::findById);

        if(fetchedBuild.isEmpty()) {
            log.warn("No Claimbuild found with name [{}]", name);
            throw ClaimBuildServiceException.noCbWithName(name);
        }

        log.debug("Successfully returning Claimbuild with name [{}]", name);
        return fetchedBuild.get();
    }

    public Set<Player> createBuiltByFromString(String builtByString) {
        log.debug("Creating builtBy from string [{}]", builtByString);

        ServiceUtils.validateStringSyntax(builtByString, new Character[]{'-'}, ClaimBuildServiceException.invalidBuiltByString(builtByString));

        String[] playerArray = builtByString.split("-");

        Set<Player> builtBy = new HashSet<>();

        for (String playerIgn : playerArray) {
            log.trace("Generating builtBy from data: [{}]", playerIgn);

            log.trace("Fetching player with ign [{}]", playerIgn);
            Player player = playerService.getPlayerByIgn(playerIgn);
            log.trace("Found player [{}]", player);

            log.trace("Adding player [{}] to HashSet", player);
            builtBy.add(player);
        }

        return builtBy;
    }

    public List<ProductionClaimbuild> createProductionSitesFromString(String prodString, @NotNull ClaimBuild claimBuild) {
        log.debug("Creating production sites from string [{}]", prodString);

        ServiceUtils.validateStringSyntax(prodString, new Character[]{':', ':', '-'}, ClaimBuildServiceException.invalidProductionSiteString(prodString));

        String[] prodSiteDataArr = prodString.split("-");

        List<ProductionClaimbuild> productionSites = new ArrayList<>();

        for (String prodSiteData : prodSiteDataArr) {
            log.trace("Generating productionSite from data: [{}]", prodSiteData);
            String[] properties = prodSiteData.split(":");

            ProductionSiteType type = null;
            String resource = properties[1];

            try {
                String inputtedType = properties[0].replace(' ', '_').toUpperCase(Locale.ROOT);
                log.trace("Getting the enum for value [{}]", inputtedType);
                type = ProductionSiteType.valueOf(inputtedType);
            }
            catch(Exception e) {
                log.warn("No Production Site type found for input [{}]", properties[0]);
                throw ClaimBuildServiceException.noProductionSiteTypeFound(properties[0]);
            }

            log.debug("Fetching Production Site with type [{}] and resource [{}]", type, resource);
            Optional<ProductionSite> fetchedProdSite = secureFind(type, resource, productionSiteRepository::findProductionSiteByTypeAndProducedResource);

            if(fetchedProdSite.isEmpty()) {
                log.warn("No Production Site found for type [{}] and resource [{}]!", type, resource);
                throw ClaimBuildServiceException.noProductionSiteFound(type.name(), resource);
            }

            long prodSiteAmount = 0L;
            try {
                prodSiteAmount = Long.parseLong(properties[2]);
                log.trace("Production Site amount: [{}]", prodSiteAmount);
            }
            catch (NumberFormatException e) {
                log.warn("Production Site String expected a number but was: [{}]", properties[2]);
                throw ClaimBuildServiceException.invalidProductionSiteString(prodString);
            }

            ProductionClaimbuildId id = new ProductionClaimbuildId(fetchedProdSite.get().getId(), claimBuild.getName());
            ProductionClaimbuild productionClaimbuild = new ProductionClaimbuild(id, fetchedProdSite.get(), claimBuild, prodSiteAmount);
            productionSites.add(productionClaimbuild);
        }
        
        return productionSites;
    }

    public List<SpecialBuilding> createSpecialBuildingsFromString(String specialBuildString) {
        log.debug("Creating special buildings from string [{}]", specialBuildString);

        ServiceUtils.validateStringSyntax(specialBuildString, new Character[]{'-'}, ClaimBuildServiceException.invalidProductionSiteString(specialBuildString));

        String[] specialBuildArr = specialBuildString.split("-");

        List<SpecialBuilding> specialBuildings = new ArrayList<>();

        for (String specialBuildName : specialBuildArr) {
            log.trace("Generating special buildings from name: [{}]", specialBuildName);

            try {
                log.trace("Trying to parse [{}] to SpecialBuilding", specialBuildName);
                SpecialBuilding specialBuilding = SpecialBuilding.valueOf(specialBuildName.replace(' ', '_').toUpperCase());
                log.trace("Found special building [{}]", specialBuilding);
                log.trace("Adding special building to list");
                specialBuildings.add(specialBuilding);
            }
            catch (Exception e) {
                log.warn("Could not find special building for inputted value [{}]", specialBuildName);
                throw ClaimBuildServiceException.noSpecialBuildingFound(specialBuildName);
            }
        }

        return specialBuildings;
    }
}
