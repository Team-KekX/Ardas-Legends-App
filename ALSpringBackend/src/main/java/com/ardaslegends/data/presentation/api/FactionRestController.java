package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.dto.UpdateFactionLeaderDto;
import com.ardaslegends.data.service.dto.faction.UpdateFactionLeaderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(FactionRestController.BASE_URL)
public class FactionRestController extends AbstractRestController {
    public static final String BASE_URL = "/api/faction";
    private static final String PATH_UPDATE_FACTION_LEADER = "/update/faction-leader";

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
}
