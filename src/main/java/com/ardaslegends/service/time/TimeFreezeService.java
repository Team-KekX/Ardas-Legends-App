package com.ardaslegends.service.time;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.*;

@Slf4j
@Service
public class TimeFreezeService {

    private boolean isTimeFrozen;

    private final ExecutorService virtualExecutorService;

    public TimeFreezeService(ExecutorService virtualExecutorService) {
        isTimeFrozen = false;
        this.virtualExecutorService = virtualExecutorService;
    }

    public Future<?> start24hTimer() {

        log.debug("Starting new 24h timer {}", Thread.currentThread());
        return virtualExecutorService.submit(() -> {
            log.info("HEELLLOO FROM {}", Thread.currentThread());
            try {
                Thread.sleep(Duration.ofSeconds(5));
                log.info("Timer is over");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void freezeTime() {
        isTimeFrozen = true;
    }

    public boolean isTimeFrozen() {
        return isTimeFrozen;
    }
}
