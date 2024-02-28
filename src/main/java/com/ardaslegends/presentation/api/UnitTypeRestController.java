package com.ardaslegends.presentation.api;

import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.service.UnitTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@Tag(name = "UnitType Controller", description = "All REST endpoints regarding unit types")
@RequestMapping(UnitTypeRestController.BASE_URL)
public class UnitTypeRestController extends AbstractRestController {

    public final static String BASE_URL = "/api/unittypes";

    private final UnitTypeService unitTypeService;

    
}
