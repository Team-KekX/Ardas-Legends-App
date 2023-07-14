package com.ardaslegends.presentation.api;

import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.api.response.player.rpchar.RpCharOwnerResponse;
import com.ardaslegends.service.RpCharService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping(RpCharRestController.BASE_URL)
public class RpCharRestController extends AbstractRestController {

    public static final String BASE_URL = "/api/rpchars";
    private static final String GET_ALL = "/all";

    private final RpCharService rpCharService;

    @GetMapping(GET_ALL)
    public ResponseEntity<Slice<RpCharOwnerResponse>> getAll(Pageable pageable) {
        log.info("Incoming rpchars getAllSliced request with pageable [{}]", pageable);

        val slicedRpchars = wrappedServiceExecution(pageable, rpCharService::getAll);
        val slicedRpCharOwnerResponse = slicedRpchars.map(RpCharOwnerResponse::new);

        return ResponseEntity.ok(slicedRpCharOwnerResponse);
    }

}
