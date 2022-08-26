package com.ardaslegends.presentation.api;

import com.ardaslegends.data.presentation.api.ArmyRestController;
import com.ardaslegends.data.presentation.api.ClaimbuildRestController;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.ClaimBuildService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ClaimbuildRestControllerTest {
    MockMvc mockMvc;
    private ClaimBuildService mockClaimbuildService
    private ClaimbuildRestController claimbuildRestController;

    @BeforeEach
    void setup() {
        mockClaimbuildService = mock(ClaimBuildService.class);
        claimbuildRestController = new ClaimbuildRestController(mockClaimbuildService);
        mockMvc = MockMvcBuilders.standaloneSetup(claimbuildRestController).build();
    }
}
