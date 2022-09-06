package com.ardaslegends.presentation.api;

import com.ardaslegends.data.presentation.api.RegionRestController;
import com.ardaslegends.data.service.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class RegionRestControllerTest {
    MockMvc mockMvc;

    private RegionService mockRegionService;
    private RegionRestController regionRestController;

    @BeforeEach
    void setup() {
        mockRegionService = mock(RegionService.class);
        regionRestController = new RegionRestController(mockRegionService);

        mockMvc = MockMvcBuilders.standaloneSetup(regionRestController).build();
    }

    @Test
    void ensureResetOwnershipRestEndpointWorksProperly() throws Exception {

        var result= mockMvc.perform((MockMvcRequestBuilders.patch("http://localhost:8080/api/region/reset-ownership")))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);

    }
}
