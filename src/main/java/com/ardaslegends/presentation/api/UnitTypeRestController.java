package com.ardaslegends.presentation.api;

import com.ardaslegends.domain.UnitType;
import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.api.response.unit.UnitTypeResponse;
import com.ardaslegends.service.UnitTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor

@Slf4j
@RestController
@Tag(name = "UnitType Controller", description = "All REST endpoints regarding unit types")
@RequestMapping(UnitTypeRestController.BASE_URL)
public class UnitTypeRestController extends AbstractRestController {

    public final static String BASE_URL = "/api/unittypes";

    private final UnitTypeService unitTypeService;

    @Operation(summary = "Get UnitTypes", 
            description = "Returns an array of unit types of the passed faction. Returns all unit types if no faction is specified",
            parameters = {@Parameter(name = "faction", 
                    description = "The faction to get the units from. Can pass multiple values to get units from multiple factions")})
    @GetMapping
    public ResponseEntity<UnitTypeResponse[]> getUnitTypes(@PathVariable(name = "faction") List<String> factions) {
        log.debug("Incoming getUnitTypes request with factions: [{}]", StringUtils.join(factions, ", "));

        log.debug("Calling unitTypeService.getByFactions");
        val unitTypes = unitTypeService.getByFactionNames(factions);

        log.debug("Building response");
        UnitTypeResponse[] response = unitTypes.stream().map(UnitTypeResponse::new).toArray(UnitTypeResponse[]::new);

        log.info("Successfully handled getUnitTypes request - sending response [{}]", StringUtils.join(response, ", "));
        return ResponseEntity.ok(response);
    }
}
