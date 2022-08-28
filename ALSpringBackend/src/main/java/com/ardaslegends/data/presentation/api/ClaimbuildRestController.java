package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.ClaimBuild;
import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.dto.claimbuild.CreateClaimBuildDto;
import com.ardaslegends.data.service.dto.claimbuilds.DeleteClaimbuildDto;
import com.ardaslegends.data.service.dto.claimbuilds.UpdateClaimbuildOwnerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(ClaimbuildRestController.BASE_URL)
public class ClaimbuildRestController extends AbstractRestController {
    public static final String BASE_URL = "/api/claimbuild";
    public static final String PATH_CREATE_CLAIMBUILD = "/create";
    public static final String PATH_UPDATE_CLAIMBUILD = "/update";
    private static final String UPDATE_CLAIMBUILD_FATION = "/update/claimbuild-faction";
    private static final String DELETE_CLAIMBUILD = "/delete";

    private final ClaimBuildService claimBuildService;

    @PostMapping(PATH_CREATE_CLAIMBUILD)
    public HttpEntity<ClaimBuild> createClaimbuild(@RequestBody CreateClaimBuildDto dto) {
        log.debug("Incoming createClaimbuild Request: Data [{}]", dto);

        log.debug("Calling claimBuildService.createClaimbuild");
        ClaimBuild claimBuild = wrappedServiceExecution(dto, true, claimBuildService::createClaimbuild);

        log.info("Sending successful createClaimbuild Request for [{}]", claimBuild.getName());
        return ResponseEntity.ok(claimBuild);
    }

    @PatchMapping(PATH_UPDATE_CLAIMBUILD)
    public HttpEntity<ClaimBuild> updateClaimbuild(@RequestBody CreateClaimBuildDto dto) {
        log.debug("Incoming updateClaimbuild Request: Data [{}]", dto);

        log.debug("Calling claimBuildService.createClaimbuild");
        ClaimBuild claimBuild = wrappedServiceExecution(dto, false, claimBuildService::createClaimbuild);

        log.info("Sending successful updateClaimbuild Request for [{}]", claimBuild.getName());
        return ResponseEntity.ok(claimBuild);
    }

    @PatchMapping(UPDATE_CLAIMBUILD_FATION)
    public HttpEntity<UpdateClaimbuildOwnerDto> updateClaimbuildOwner(@RequestBody UpdateClaimbuildOwnerDto dto) {
        log.debug("Incoming update Claimbuild Owner Request with data [{}]", dto);

        log.trace("Calling wrappedServiceExecution of setOwnerFaction");
        var result = wrappedServiceExecution(dto, claimBuildService::setOwnerFaction);

        log.trace("Building response Dto");
        UpdateClaimbuildOwnerDto response = new UpdateClaimbuildOwnerDto(result.getName(), result.getOwnedBy().getName());

        log.info("Sending successful response [{}] to bot!", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(DELETE_CLAIMBUILD)
    public HttpEntity<DeleteClaimbuildDto> deleteClaimbuild(@RequestBody DeleteClaimbuildDto dto) {
        log.debug("Incoming delete Claimbuild Request with data [{}]", dto);

        log.trace("Calling wrappedServiceExecution of deleteClaimbuild");
        var result = wrappedServiceExecution(dto, claimBuildService::deleteClaimbuild);

        log.trace("Building body Dto");
        DeleteClaimbuildDto body = new DeleteClaimbuildDto(result.getName(),
                 result.getStationedArmies().stream().map(Army::getName).collect(Collectors.toList()),
                 result.getCreatedArmies().stream().map(Army::getName).collect(Collectors.toList()));

        log.info("Creating response with body [{}]", body);
        var response = ResponseEntity.ok(body);
        return response;
    }
}
