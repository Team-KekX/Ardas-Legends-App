package com.ardaslegends.presentation.api;

import com.ardaslegends.domain.war.War;
import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.api.response.war.ActiveWarResponse;
import com.ardaslegends.presentation.api.response.war.PaginatedWarsResponse;
import com.ardaslegends.service.dto.player.DiscordIdDto;
import com.ardaslegends.service.dto.war.CreateWarDto;
import com.ardaslegends.service.war.WarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor

@Slf4j
@RestController
@Tag(name = "War Controller", description = "REST Endpoints concerning Wars")
@RequestMapping(WarRestController.BASE_URL)
public class WarRestController extends AbstractRestController {
    public static final String BASE_URL = "/api/wars";
    public static final String CREATE_WAR = "/declare";
    public static final String END = "/end"; //Will be used later on when faction leaders can end war
    public static final String FORCE_END = END + "/force"; //Staff only

    private final WarService warService;

    @Operation(summary = "Get Wars Paginated", description = "Retrieves a Page with a set of elements, parameters define the size, which Page you want and how its sorted")
    @GetMapping
    public ResponseEntity<Page<PaginatedWarsResponse>> getWarsPaginated(Pageable pageable) {
        log.debug("Incoming getWarsPaginated Request, paginated data [{}]", pageable);

        Page<War> page = wrappedServiceExecution(pageable, warService::getWars);
        log.debug(page.toString());

        Page<PaginatedWarsResponse> response = page.map(PaginatedWarsResponse::new);

        return ResponseEntity.ok(response);
    }


    @PostMapping(CREATE_WAR)
    public ResponseEntity<ActiveWarResponse> createWar(@RequestBody CreateWarDto dto) {
        log.debug("Incoming declareWar Request: Data [{}]", dto);

        War createWarResult = wrappedServiceExecution(dto, warService::createWar);
        ActiveWarResponse response = new ActiveWarResponse(createWarResult);

        log.debug("Result from service is [{}]", response);

        log.info("Sending successful createWar Request for [{}]", dto.nameOfWar());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(FORCE_END)
    public ResponseEntity<ActiveWarResponse> forceEndWar(@RequestParam String warName, @RequestBody DiscordIdDto dto) {
        log.debug("Incoming declareWar Request: Data [{}]", dto);

//        War createWarResult = wrappedServiceExecution(dto, warService::createWar);
//        ActiveWarResponse response = new ActiveWarResponse(createWarResult);
//
//        log.debug("Result from service is [{}]", response);
//
//        log.info("Sending successful createWar Request for [{}]", dto.nameOfWar());
//        return ResponseEntity.ok(response);
        return null;
    }
}
