package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.dto.UpdateFactionLeaderDto;
import com.ardaslegends.data.service.dto.faction.UpdateFactionLeaderResponseDto;
import com.ardaslegends.data.service.dto.faction.UpdateStockpileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(FactionRestController.BASE_URL)
public class FactionRestController extends AbstractRestController {
    public static final String BASE_URL = "/api/faction";
    private static final String PATH_UPDATE_FACTION_LEADER = "/update/faction-leader";
    private static final String PATH_UPDATE_STOCKPILE_ADD = "/update/stockpile/add";
    private static final String PATH_UPDATE_STOCKPILE_REMOVE = "/update/stockpile/remove";
    private static final String PATH_GET_STOCKPILE_INFO = "/get/stockpile/info/{faction}";

    private final FactionService factionService;

    @PatchMapping(PATH_UPDATE_FACTION_LEADER)
    public ResponseEntity<UpdateFactionLeaderResponseDto> setFactionLeader(@RequestBody UpdateFactionLeaderDto dto) {
        log.debug("Incoming update faction-leader request with data [{}]", dto);

        log.trace("Calling wrappedServiceExecution setFactionLeader");
        var result = factionService.setFactionLeader(dto);

        log.trace("Result: [faction:{}] and new leader [discId:{}]. Dto discId [{}]", result.getName(), result.getLeader().getDiscordID(), dto.targetDiscordId());
        log.trace("Building response body");

        UpdateFactionLeaderResponseDto body = new UpdateFactionLeaderResponseDto(result.getName() ,result.getLeader().getIgn());

        log.trace("Body [{}]", body.toString());
        log.info("Sending successful update faction-leader request to bot! Body:[{}]", body);
        return ResponseEntity.ok(body);
    }

    @PatchMapping(PATH_UPDATE_STOCKPILE_ADD)
    public ResponseEntity<UpdateStockpileDto> addStockpile(@RequestBody UpdateStockpileDto dto) {
        log.debug("Incoming add to stockpile request with data [{}]", dto);

        log.trace("Calling wrappedServiceExecution addToStockpile");
        var result = factionService.addToStockpile(dto);

        UpdateStockpileDto body = getUpdateStockpileDto(result);
        log.info("Sending successful update add to stockpile request to bot! Body:[{}]", body);
        return ResponseEntity.ok(body);
    }
    @PatchMapping(PATH_UPDATE_STOCKPILE_REMOVE)
    public ResponseEntity<UpdateStockpileDto> removeFromStockpile(@RequestBody UpdateStockpileDto dto) {
        log.debug("Incoming remove from stockpile request with data [{}]", dto);

        log.trace("Calling wrappedServiceExecution removeFromStockpile");
        var result = factionService.removeFromStockpile(dto);

        UpdateStockpileDto body = getUpdateStockpileDto(result);
        log.info("Sending successful update remove from stockpile request to bot! Body:[{}]", body);
        return ResponseEntity.ok(body);
    }

    @GetMapping(PATH_GET_STOCKPILE_INFO)
    public ResponseEntity<UpdateStockpileDto> getStockpileInfo(@PathVariable("faction") String faction) {
        log.debug("Incoming getStockpile info request with data [{}]", faction);

        log.trace("Calling wrappedServiceExecution getFactionByName");
        var result = factionService.getFactionByName(faction);

        UpdateStockpileDto body = getUpdateStockpileDto(result);
        log.info("Sending successful get stockpile info request to bot! Body:[{}]", body);
        return ResponseEntity.ok(body);
    }

    private static UpdateStockpileDto getUpdateStockpileDto(Faction result) {
        log.trace("Result faction [{}] and stockpile [{}]", result.getName(), result.getFoodStockpile());
        log.trace("Building response body");

        UpdateStockpileDto body = new UpdateStockpileDto(result.getName(), result.getFoodStockpile());

        log.trace("Body [{}]", body.toString());
        return body;
    }

}
