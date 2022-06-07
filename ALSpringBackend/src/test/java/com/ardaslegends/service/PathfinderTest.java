package com.ardaslegends.service;

import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.MovementService;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathfinderTest {

    private RegionRepository mockRepository;

    @BeforeAll
    void setup() {
        mockRepository = mock(RegionRepository.class);
    }
}
