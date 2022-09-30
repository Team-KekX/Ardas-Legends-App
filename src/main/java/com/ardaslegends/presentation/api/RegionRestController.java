package com.ardaslegends.presentation.api;

import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.service.RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(RegionRestController.BASE_URL)
public class RegionRestController extends AbstractRestController {
    public static final String BASE_URL = "/api/region";
    public static final String RESET_OWNERSHIP = "/reset-ownership";

    private final RegionService regionService;

    @PatchMapping(RESET_OWNERSHIP)
    public ResponseEntity<Void> resetOwnershipChanged() {
        log.debug("Incoming reset ownership-changed request");

        log.debug("Calling wrappedServiceExecution regionService.resetOwnership");
        wrappedServiceExecution(regionService::resetHasOwnership);

        log.info("Returning status code ok for reset-ownership");
        return ResponseEntity.ok(null);
    }

}
