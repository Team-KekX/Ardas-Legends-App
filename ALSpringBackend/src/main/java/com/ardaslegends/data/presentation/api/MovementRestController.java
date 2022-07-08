package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.domain.Movement;
import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(MovementRestController.BASE_URL)
public class MovementRestController extends AbstractRestController {

    private final MovementService movementService;

    public static final String BASE_URL = "/api/movement";

    public static final String PATH_MOVE_CHAR = "/move-char";

    @PostMapping(PATH_MOVE_CHAR)
    public HttpEntity<Movement> moveRoleplayCharacter(@RequestBody MoveRpCharDto dto) {

        log.debug("Incoming Post Request to create rp char movement, data [{}]", dto);
        log.trace("WrappedServiceExecution of moveRoleplayCharacter function");
        Movement movement = wrappedServiceExecution(dto, movementService::createRpCharMovement);

        log.info("Successfully handled request for creating rpchar movement!");
        return ResponseEntity.ok(movement);
    }
}
