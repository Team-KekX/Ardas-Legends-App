package com.ardaslegends.presentation.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "RPChar Controller", description = "All REST endpoints regarding Roleplay Characters")
@RequestMapping(RPCharRestController.BASE_URL)
public class RPCharRestController {

    public final static String BASE_URL = "/api/rpchars";

}
