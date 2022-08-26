package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.ClaimBuild;
import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.service.dto.claimbuilds.UpdateClaimbuildOwnerDto;
import com.ardaslegends.data.domain.Region;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.dto.claimbuild.CreateClaimBuildDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.exceptions.claimbuild.ClaimBuildServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class ClaimBuildService extends AbstractService<ClaimBuild, ClaimBuildRepository> {

    private final ClaimBuildRepository claimbuildRepository;
    private final RegionRepository regionRepository;

    private final FactionService factionService;

    public UpdateClaimbuildOwnerDto setOwnerFaction(UpdateClaimbuildOwnerDto dto) {
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
        UpdateClaimbuildOwnerDto returnDto = new UpdateClaimbuildOwnerDto(claimBuild.getName(), claimBuild.getOwnedBy().getName());
        return returnDto;
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
        if(existingClaimbuild.isPresent()) {
            var claimbuild = existingClaimbuild.get();
            log.warn("Claimbuild with name [{}] already exists (region [{}] - owned by [{}])", claimbuild.getName(), claimbuild.getRegion(), claimbuild.getOwnedBy());
            throw ClaimBuildServiceException.cbAlreadyExists(claimbuild.getName(), claimbuild.getRegion().getId(), claimbuild.getOwnedBy().getName());
        }

        log.debug("Getting the inputted region");
        log.trace("Fetching the region");
        Optional<Region> fetchedRegion = secureFind(dto.regionId(), regionRepository::findById);
        log.trace("Checking if region exists");
        if(fetchedRegion.isEmpty()) {
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

    public void validateUnitString(String unitString, Character[] syntaxChars, ServiceException exceptionToThrow) {
        log.debug("Validating unitString [{}]", unitString);

        // Is also true at the start, from then every time a expectedChar is switched
        boolean firstCharAfterExpected = true; //says if the current character is the first character after the last expected one
        boolean possibleEnd = false;

        log.trace("Starting validation, unitString length: [{}]", unitString.length());

        int currentExpectedCharIndex = 0; //index of the currently expected char
        char expectedChar = syntaxChars[0]; //Set the expectedChar to first in array

        for(int i = 0; i < unitString.length(); i++) {
            log.trace("Index: [{}]", i);
            log.trace("Expected next syntax char [{}]", expectedChar);
            char currentChar = unitString.charAt(i);
            possibleEnd = false;
            log.trace("Current char: [{}]", currentChar);

            if(currentChar == expectedChar) {
                log.trace("Current char {} is expected char {}", currentChar, expectedChar);

                //Check if current char is second last
                if(currentExpectedCharIndex == (syntaxChars.length - 2))
                    possibleEnd = true;
                /*
                Increment the currentExpectedCharIndex so we expect the next character
                If the index has reached the end of the array, start over from 0
                 */
                currentExpectedCharIndex++;
                if(currentExpectedCharIndex == syntaxChars.length)
                    currentExpectedCharIndex = 0;
                log.trace("Incremented the currentExpectedCharIndex to {}", currentExpectedCharIndex);

                expectedChar = syntaxChars[currentExpectedCharIndex];
            }
            else if(Arrays.asList(syntaxChars).contains(currentChar)) {
                log.warn("Char [{}] at [{}] has created an error in string [{}], next expected was [{}]", currentChar, i, unitString, expectedChar);
                throw exceptionToThrow;
            }


            if((i + 1) == unitString.length() && !possibleEnd) {
                log.warn("String reached its end without having finished syntax!");
                throw exceptionToThrow;
            }
        }
    }

}
