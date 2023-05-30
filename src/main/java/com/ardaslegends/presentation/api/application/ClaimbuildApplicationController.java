package com.ardaslegends.presentation.api.application;

import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.api.response.applications.ClaimbuildApplicationResponse;
import com.ardaslegends.service.applications.ClaimbuildApplicationService;
import com.ardaslegends.service.dto.applications.CreateClaimbuildApplicationDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ClaimbuildApplicationController.BASE_URL)
public class ClaimbuildApplicationController extends AbstractRestController {
    public static final String BASE_URL = "/api/applications/claimbuild";

    private final ClaimbuildApplicationService claimbuildApplicationService;

    @Operation(summary = "Creates a Claimbuild Application")
    @PostMapping
    public HttpEntity<ClaimbuildApplicationResponse> createClaimbuildApplication(CreateClaimbuildApplicationDto applicationDto) {
        log.debug("Incoming createClaimbuildApplication Request: Data [{}]", applicationDto);

        val application = wrappedServiceExecution(applicationDto, claimbuildApplicationService::createClaimbuildApplication);

        return ResponseEntity.ok(new ClaimbuildApplicationResponse(application));
    }
}
