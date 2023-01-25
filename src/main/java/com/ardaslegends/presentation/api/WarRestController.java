package com.ardaslegends.presentation.api;

import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.api.response.war.CreateWarResponse;
import com.ardaslegends.service.dto.war.CreateWarDto;
import com.ardaslegends.service.war.WarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(WarRestController.BASE_URL)
public class WarRestController extends AbstractRestController {
    public static final String BASE_URL = "/api/war";
    public static final String CREATE_WAR = "/declare";

    private final WarService warService;

    @PostMapping(CREATE_WAR)
    public ResponseEntity<CreateWarResponse> createWar(@RequestBody CreateWarDto dto) {
        // TODO Implement ig
        return null;
    }

}
