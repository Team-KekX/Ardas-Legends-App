package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.dto.claimbuilds.UpdateClaimbuildOwnerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(ClaimbuildRestController.BASE_URL)
public class ClaimbuildRestController extends AbstractRestController {
    public static final String BASE_URL = "/api/claimbuild";
    private static final String UPDATE_CLAIMBUILD_FATION = "update/claimbuild-faction";

    private final ClaimBuildService claimBuildService;

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
}
