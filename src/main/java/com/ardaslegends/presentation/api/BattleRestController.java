package com.ardaslegends.presentation.api;

import com.ardaslegends.service.war.BattleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@Tag(name = "Battle Controller", description = "REST endpoints concerning Battles")
@RequestMapping(BattleRestController.BASE_URL)
public class BattleRestController {
    public static final String BASE_URL = "/api/battles";

    private final BattleService battleService;

}
