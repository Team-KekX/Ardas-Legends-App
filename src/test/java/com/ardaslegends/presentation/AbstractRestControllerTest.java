package com.ardaslegends.presentation;

import com.ardaslegends.domain.Player;
import com.ardaslegends.presentation.exceptions.BadArgumentException;
import com.ardaslegends.presentation.exceptions.InternalServerException;
import com.ardaslegends.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
public class AbstractRestControllerTest {


    Function<Integer, Player> mockFunction;
    AbstractRestController abstractController;

    @BeforeEach
    void setup() {
        mockFunction = mock(Function.class);
        abstractController = mock(AbstractRestController.class, CALLS_REAL_METHODS);
    }
    
}
