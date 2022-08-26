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

    public ClaimBuild createClaimbuild(CreateClaimBuildDto dto) {
        log.debug("Trying to create claimbuild with data [{}]", dto);

        log.trace("Validating data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Checking if claimbuild with name [{}] already exists", dto.name());
        log.trace("Fetching claimbuild with name [{}]", dto.name());
        Optional<ClaimBuild> existingClaimbuild = secureFind(dto.name(), claimbuildRepository::findById);
        log.trace("Checking if a claimbuild was found");
        if (existingClaimbuild.isPresent()) {
            var claimbuild = existingClaimbuild.get();
            log.warn("Claimbuild with name [{}] already exists (region [{}] - owned by [{}])", claimbuild.getName(), claimbuild.getRegion(), claimbuild.getOwnedBy());
            throw ClaimBuildServiceException.cbAlreadyExists(claimbuild.getName(), claimbuild.getRegion().getId(), claimbuild.getOwnedBy().getName());
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


        return null;
    }
    @Transactional(readOnly = false)
    public ClaimBuild deleteClaimbuild(DeleteClaimbuildDto dto) {
        log.debug("Trying to delete claimbuild with name [{}]", dto.claimbuildName());

        ServiceUtils.checkNulls(dto, List.of("claimbuildName"));
        ServiceUtils.checkBlanks(dto, List.of("claimbuildName"));

        ClaimBuild claimBuild = getClaimBuildByName(dto.claimbuildName());

        secureDelete(claimBuild, claimbuildRepository);
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

            productionSites.add(new ProductionClaimbuild(fetchedProdSite.get(), claimBuild, prodSiteAmount));
        }
        
        return productionSites;
    }
}
