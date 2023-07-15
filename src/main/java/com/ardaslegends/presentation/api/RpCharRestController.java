package com.ardaslegends.presentation.api;

import com.ardaslegends.domain.RPChar;
import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.api.response.player.rpchar.RpCharOwnerResponse;
import com.ardaslegends.service.RpCharService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@Tag(name = "RPChar Controller", description = "All REST endpoints regarding Roleplay Characters")
@RequestMapping(RpCharRestController.BASE_URL)
public class RpCharRestController extends AbstractRestController {

    public final static String BASE_URL = "/api/rpchars";

    private final RpCharService rpCharService;

    @Operation(summary = "Get RpChars Paginated", description = "Returns a Page of RpChars")
    @GetMapping
    public ResponseEntity<Slice<RpCharOwnerResponse>> getAll(Pageable pageable) {
        log.info("Incoming get all rpchars Request");

        log.debug("Calling rpCharService.getAll...");
        Slice<RPChar> rpChars = wrappedServiceExecution(pageable, rpCharService::getAll);
        log.debug("Received slice of RpChars from service");

        log.debug("Building RpChar response");
        Slice<RpCharOwnerResponse> rpCharOwnerResponses = rpChars.map(RpCharOwnerResponse::new);
        log.debug("Built response [{}]", rpCharOwnerResponses);

        log.info("Successfully handled get all RpChars request - returning data [{}]", rpCharOwnerResponses);
        return ResponseEntity.ok(rpCharOwnerResponses);
    }

}
