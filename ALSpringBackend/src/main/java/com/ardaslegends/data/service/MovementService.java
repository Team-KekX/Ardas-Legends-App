package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.Movement;
import com.ardaslegends.data.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Slf4j
@Service
@Transactional(readOnly = true)
public class MovementService extends AbstractService<Movement, MovementRepository>{

    private final MovementRepository movementRepository;

    //TODO: Add test
    @Transactional(readOnly = false)
    public Movement createMovement(Movement movement) {
        log.debug("Saving movement {}", movement);

        secureSave(movement, movementRepository);

        log.info("Successfully created movement: {}", movement);
        return movement;
    }
}
