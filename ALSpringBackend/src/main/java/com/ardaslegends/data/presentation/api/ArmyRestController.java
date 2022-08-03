package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.BindArmyDto;
import com.ardaslegends.data.service.dto.army.CreateArmyDto;
import com.ardaslegends.data.service.dto.army.DeleteArmyDto;
import com.ardaslegends.data.service.dto.army.UpdateArmyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(ArmyRestController.BASE_URL)
public class ArmyRestController extends AbstractRestController {
    public static final String BASE_URL = "/api/army";
    private static final String PATH_CREATE_ARMY = "/create";
    private static final String PATH_BIND_ARMY = "/bind-army";
    private static final String PATH_UNBIND_ARMY = "/unbind-army";
    private static final String PATH_DISBAND_ARMY = "/disband-army";
    private static final String PATH_DELETE_ARMY = "/delete-army";
    private static final String PATH_SET_FREE_TOKENS = "/set-free-tokens";

    private final ArmyService armyService;

    @PostMapping(PATH_CREATE_ARMY)
    public HttpEntity<Army> createArmy(@RequestBody CreateArmyDto dto) {
        log.debug("Incoming createArmy Request: Data [{}]", dto);

        log.debug("Calling ArmyService.createArmy");
        Army createdArmy = wrappedServiceExecution(dto, armyService::createArmy);

        log.info("Sending successful createArmy Request for [{}]", createdArmy.getName());
        return ResponseEntity.ok(createdArmy);
    }

    @PatchMapping(PATH_BIND_ARMY)
    public HttpEntity<Army> bindArmy(@RequestBody BindArmyDto dto) {
        log.debug("Incoming bindArmy Request: Data [{}]", dto);

        log.debug("Calling ArmyService.bind()");
        Army boundArmy = wrappedServiceExecution(dto, armyService::bind);

        log.info("Sending successful bindArmy request for [{}]", boundArmy.getName());
        return ResponseEntity.ok(boundArmy);
    }

    @PatchMapping(PATH_UNBIND_ARMY)
    public HttpEntity<Army> unbindArmy(@RequestBody BindArmyDto dto) {
        log.debug("Incoming unbindArmy Request: Data [{}]", dto);

        log.debug("Calling ArmyService.unbind()");
        Army unboundArmy = wrappedServiceExecution(dto, armyService::unbind);

        log.info("Sending successful unbindArmy request for [{}]", unboundArmy.getName());
        return ResponseEntity.ok(unboundArmy);
    }

    @DeleteMapping(PATH_DISBAND_ARMY)
    public HttpEntity<Army> disbandArmy(@RequestBody DeleteArmyDto dto) {
        log.debug("Incoming disbandArmy Request: Data [{}]", dto);

        log.debug("Calling ArmyService.unbind()");
        Army disbandedArmy = wrappedServiceExecution(dto, false, armyService::disband);

        log.info("Sending successful disbandArmy request for [{}]", disbandedArmy.getName());
        return ResponseEntity.ok(disbandedArmy);
    }

    @DeleteMapping(PATH_DELETE_ARMY)
    public HttpEntity<Army> deleteArmy(@RequestBody DeleteArmyDto dto) {
        log.debug("Incoming deleteArmy Request: Data [{}]", dto);

        log.debug("Calling ArmyService.disband()");
        Army deletedArmy = wrappedServiceExecution(dto, true, armyService::disband);

        log.info("Sending successful deleteArmy request for [{}]", deletedArmy.getName());
        return ResponseEntity.ok(deletedArmy);
    }

    @PatchMapping(PATH_SET_FREE_TOKENS)
    public HttpEntity<Army> setFreeArmyTokens(@RequestBody UpdateArmyDto dto) {
        log.debug("Incoming setFreeArmyTokens Request: Data [{}]", dto);

        log.debug("Calling ArmyService.setFreeArmyTokens()");
        Army deletedArmy = wrappedServiceExecution(dto, armyService::setFreeArmyTokens);

        log.info("Sending successful setFreeArmyTokens request for [{}]", deletedArmy.getName());
        return ResponseEntity.ok(deletedArmy);
    }
}
