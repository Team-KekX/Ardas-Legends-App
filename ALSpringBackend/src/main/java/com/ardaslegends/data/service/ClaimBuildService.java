package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.ClaimBuild;
import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.domain.UnitType;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.service.dto.army.UpdateArmyDto;
import com.ardaslegends.data.service.dto.claimbuilds.UpdateClaimbuildDto;
import com.ardaslegends.data.service.exceptions.claimbuild.ClaimBuildServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class ClaimBuildService extends AbstractService<ClaimBuild, ClaimBuildRepository> {

    private final ClaimBuildRepository claimbuildRepository;
    private final FactionService factionService;

    public ClaimBuild setOwnerFaction(UpdateClaimbuildDto dto) {
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

}
