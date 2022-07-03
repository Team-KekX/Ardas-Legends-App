package com.ardaslegends.data.presentation.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(MovementRestController.BASE_URL)
public class MovementRestController {

    public static final String BASE_URL = "/api/movement";
}
