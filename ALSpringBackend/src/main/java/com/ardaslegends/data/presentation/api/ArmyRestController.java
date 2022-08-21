package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private static final String PATH_START_HEALING = "/heal-start";
    private static final String PATH_STOP_HEALING = "/heal-stop";
    private static final String PATH_STATION = "/station";
    private static final String PATH_UNSTATION = "/unstation";
    private static final String PATH_SET_FREE_TOKENS = "/set-free-tokens";
    private static final String PATH_PICK_SIEGE = "/pick-siege";
    private static final String PATH_UPKEEP = "/upkeep";
    private static final String PATH_UPKEEP_PER_FACTION = "/upkeep/{faction}";


    private final ArmyService armyService;

    @PostMapping(PATH_CREATE_ARMY)
    public HttpEntity<Army> createArmy(@RequestBody CreateArmyDto dto) {
        log.debug("Incoming createArmy Request: Data [{}]", dto);

        var units = armyService.convertUnitInputIntoUnits(dto.unitString());
        CreateArmyDto dtoWithUnits = new CreateArmyDto(dto.executorDiscordId(), dto.name(), dto.armyType(), dto.claimBuildName(), units);
        log.debug("Calling ArmyService.createArmy");
        Army createdArmy = wrappedServiceExecution(dtoWithUnits, armyService::createArmy);

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

    @PatchMapping(PATH_START_HEALING)
    public HttpEntity<Army> startHealing(@RequestBody UpdateArmyDto dto) {
        log.debug("Incoming start healing Request: Data [{}]", dto);

        log.debug("Calling healStart()");
        Army modifiedArmy = wrappedServiceExecution(dto, armyService::healStart);

        log.info("Sending successful start healing Request for [{}]", modifiedArmy.toString());
        return ResponseEntity.ok(modifiedArmy);
    }

    @PatchMapping(PATH_STOP_HEALING)
    public HttpEntity<Army> stopHealing(@RequestBody UpdateArmyDto dto) {
        log.debug("Incoming stop healing Request: Data [{}]", dto);

        log.debug("Calling healStop()");
        Army modifiedArmy = wrappedServiceExecution(dto, armyService::healStop);

        log.info("Sending successful stop healing Request for [{}]", modifiedArmy.toString());
        return ResponseEntity.ok(modifiedArmy);
    }

    @PatchMapping(PATH_STATION)
    public HttpEntity<Army> station(@RequestBody StationDto dto) {
        log.debug("Incoming station request: Data [{}]", dto);

        log.debug("Calling station()");
        Army modifiedArmy = wrappedServiceExecution(dto, armyService::station);

        log.info("Sending successful station request for [{}]", modifiedArmy.toString());
        return ResponseEntity.ok(modifiedArmy);
    }

    @PatchMapping(PATH_UNSTATION)
    public HttpEntity<Army> unstation(@RequestBody StationDto dto) {
        log.debug("Incoming station request: Data [{}]", dto);

        log.debug("Calling unstation()");
        Army modifiedArmy = wrappedServiceExecution(dto, armyService::unstation);

        log.info("Sending successful unstation request for [{}]", modifiedArmy.toString());
        return ResponseEntity.ok(modifiedArmy);
    }

    @PatchMapping(PATH_SET_FREE_TOKENS)
    public HttpEntity<Army> setFreeArmyTokens(@RequestBody UpdateArmyDto dto) {
        log.debug("Incoming setFreeArmyTokens Request: Data [{}]", dto);

        log.debug("Calling ArmyService.setFreeArmyTokens()");
        Army deletedArmy = wrappedServiceExecution(dto, armyService::setFreeArmyTokens);

        log.info("Sending successful setFreeArmyTokens request for [{}]", deletedArmy.getName());
        return ResponseEntity.ok(deletedArmy);
    }

    @PatchMapping(PATH_PICK_SIEGE)
    public HttpEntity<Army> pickSiege(@RequestBody PickSiegeDto dto) {
        log.debug("Incoming pickSiege Request: Data [{}]", dto);

        log.debug("Calling ArmyService.pickSiege()");
        Army army = wrappedServiceExecution(dto, armyService::pickSiege);

        log.info("Sending successful pickSiege request for [{}]", army.getName());
        return ResponseEntity.ok(army);
    }

    @GetMapping(PATH_UPKEEP)
    public HttpEntity<List<UpkeepDto>> upkeep() {
        log.debug("Incoming upkeep request");

        log.debug("Calling ArmyService.upkeep()");
        var upkeepList = wrappedServiceExecution(armyService::upkeep);

        log.info("Sending successful upkeep request");
        return ResponseEntity.ok(upkeepList);
    }

    @GetMapping(PATH_UPKEEP_PER_FACTION)
    public HttpEntity<UpkeepDto> upkeepPerFaction(@PathVariable("faction") String factionName) {
        log.debug("Incoming upkeep per faction request for faction: [{}]", factionName);

        log.debug("Calling ArmyService.upkeepPerFaction()");
        UpkeepDto result = wrappedServiceExecution(factionName, armyService::getUpkeepOfFaction);

        log.info("Sending successful upkeepPerFaction Request for faction: [{}]", factionName);
        return ResponseEntity.ok(result);
    }
}
