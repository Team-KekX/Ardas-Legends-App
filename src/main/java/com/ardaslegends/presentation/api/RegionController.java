package com.ardaslegends.presentation.api;

import com.ardaslegends.domain.RegionType;
import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.api.response.region.RegionResponse;
import com.ardaslegends.presentation.api.response.region.RegionResponseDetailed;
import com.ardaslegends.service.RegionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(RegionController.BASE_URL)
public class RegionController extends AbstractRestController {
    public static final String BASE_URL = "/api/region";
    private static final String RESET_OWNERSHIP = "/reset-ownership";
    private static final String GET_ALL = "/all";
    private static final String GET_ALL_DETAILED = "/all/detailed";
    private static final String GET_REGION_TYPES = "/types";

    private final RegionService regionService;

    @GetMapping(GET_ALL)
    public ResponseEntity<RegionResponse[]> getAll() {
        log.info("Incoming getAll Request");

        val regions = wrappedServiceExecution(regionService::getAll);
        val regionsResponse = regions.stream()
                .map(RegionResponse::new)
                .toArray(RegionResponse[]::new);

        return ResponseEntity.ok(regionsResponse);
    }

    @GetMapping(GET_ALL_DETAILED)
    public ResponseEntity<RegionResponseDetailed[]> getAllDetailed() {
        log.info("Incoming getAllDetailed Request");

        val regions = wrappedServiceExecution(regionService::getAll);
        val regionsResponse = regions.stream()
                .map(RegionResponseDetailed::new)
                .toArray(RegionResponseDetailed[]::new);

        return ResponseEntity.ok(regionsResponse);
    }

    @GetMapping(GET_REGION_TYPES)
    public HttpEntity<String[]> getRegionTypes(){
        log.info("Incoming getRegionTypes Request");

        val regionTypesStringArray = Arrays.stream(RegionType.values())
                .map(RegionType::getName)
                .toArray(String[]::new);

        return ResponseEntity.ok(regionTypesStringArray);
    }

    @PatchMapping(RESET_OWNERSHIP)
    public ResponseEntity<Void> resetOwnershipChanged() {
        log.info("Incoming reset ownership-changed request");

        log.debug("Calling wrappedServiceExecution regionService.resetOwnership");
        wrappedServiceExecution(regionService::resetHasOwnership);

        log.debug("Returning status code ok for reset-ownership");
        return ResponseEntity.ok(null);
    }

}
