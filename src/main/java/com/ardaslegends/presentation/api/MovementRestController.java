package com.ardaslegends.presentation.api;

import com.ardaslegends.domain.Movement;
import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.api.response.movement.MovementResponse;
import com.ardaslegends.service.MovementService;
import com.ardaslegends.service.dto.army.MoveArmyDto;
import com.ardaslegends.service.dto.player.DiscordIdDto;
import com.ardaslegends.service.dto.player.rpchar.MoveRpCharDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
    public static final String PATH_MOVE_ARMY = "/move-army-or-company";
    public static final String PATH_CANCEL_CHAR_MOVEMENT = "/cancel-char-move";
    public static final String PATH_CANCEL_ARMY_MOVEMENT = "/cancel-army-move";

    public static final String PATH_CALCULATE_ARMY_MOVEMENT = "/calculate/army";
    public static final String PATH_CALCULATE_CHAR_MOVEMENT = "/calculate/char";

    @GetMapping(PATH_CALCULATE_ARMY_MOVEMENT)
    public HttpEntity<MovementResponse> calculateArmyMove(MoveArmyDto dto) {
        log.debug("Incoming get request for army movement calculation [{}]", dto);

        log.trace("WrappedServiceExecution of calculateArmyMovement function");
        val movement = wrappedServiceExecution(dto, movementService::calculateArmyMovement);
        log.debug("Creating MovementResponse");
        MovementResponse response = new MovementResponse(movement);

        log.info("Successfully handled request - calculated army movement!");
        return ResponseEntity.ok(response);
    }

    @GetMapping(PATH_CALCULATE_CHAR_MOVEMENT)
    public HttpEntity<MovementResponse> calculateCharMove(MoveRpCharDto dto) {
        log.debug("Incoming get request for char movement calculation [{}]", dto);

        log.trace("WrappedServiceExecution of calculateRpCharMovement function");
        val movement = wrappedServiceExecution(dto, movementService::calculateRpCharMovement);
        log.debug("Creating MovementResponse");
        MovementResponse response = new MovementResponse(movement);

        log.info("Successfully handled request - calculated char movement!");
        return ResponseEntity.ok(response);
    }
    @PostMapping(PATH_MOVE_CHAR)
    public HttpEntity<MovementResponse> moveRoleplayCharacter(@RequestBody MoveRpCharDto dto) {

        log.debug("Incoming Post Request to create rp char movement, data [{}]", dto);
        log.trace("WrappedServiceExecution of createRpCharMovement function");
        Movement movement = wrappedServiceExecution(dto, movementService::createRpCharMovement);
        log.debug("Creating MovementResponse");
        MovementResponse response = new MovementResponse(movement);

        log.info("Successfully handled request for creating rpchar movement!");
        return ResponseEntity.ok(response);
    }

    @PatchMapping(PATH_CANCEL_CHAR_MOVEMENT)
    public HttpEntity<MovementResponse> cancelRoleplayCharacterMove(@RequestBody DiscordIdDto dto) {

        log.debug("Incoming Post Request to cancel rp char movement, data [{}]", dto);
        log.trace("WrappedServiceExecution of cancelRpCharMovement function");
        Movement movement = wrappedServiceExecution(dto, movementService::cancelRpCharMovement);
        log.debug("Creating MovementResponse");
        MovementResponse response = new MovementResponse(movement);

        log.info("Successfully handled request for cancelling rpchar movement!");
        return ResponseEntity.ok(response);
    }

    @PostMapping(PATH_MOVE_ARMY)
    public HttpEntity<MovementResponse> moveArmy(@RequestBody MoveArmyDto dto) {
        log.debug("Incoming Post Request to create army movement, data [{}]", dto);

        log.trace("WrappedServiceExecution of createArmyMovement function");
        Movement movement = wrappedServiceExecution(dto, movementService::createArmyMovement);
        log.debug("Creating MovementResponse");
        MovementResponse response = new MovementResponse(movement);

        log.info("Successfully handled request for creating army movement!");
        return ResponseEntity.ok(response);
    }

    @PatchMapping(PATH_CANCEL_ARMY_MOVEMENT)
    public HttpEntity<MovementResponse> cancelArmyMove(@RequestBody MoveArmyDto dto) {

        log.debug("Incoming Post Request to cancel army movement, data [{}]", dto);
        log.trace("WrappedServiceExecution of cancelArmyMovement function");
        Movement movement = wrappedServiceExecution(dto, movementService::cancelArmyMovement);
        log.debug("Creating MovementResponse");
        MovementResponse response = new MovementResponse(movement);

        log.info("Successfully handled request for cancelling army movement!");
        return ResponseEntity.ok(response);
    }
}
