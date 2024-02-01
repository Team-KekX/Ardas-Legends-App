package com.ardaslegends.service.time;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.*;

@Slf4j
@Service
public class TimeFreezeService implements Sleep {

    private boolean isTimeFrozen;

    private final ExecutorService virtualExecutorService;

    public TimeFreezeService(ExecutorService virtualExecutorService) {
        isTimeFrozen = false;
        this.virtualExecutorService = virtualExecutorService;
    }

    public <T> Future<T> start24hTimer(Callable<T> callback) {
        log.debug("Call of start24hTimer, Thread before timer: [{}]", Thread.currentThread());

        return virtualExecutorService.submit(() -> {
            log.info("Starting new 24h timer on thread [{}]", Thread.currentThread());
            try {
                sleep(Duration.ofHours(24));
                log.info("Timer is over - calling callback [{}]", callback.toString());
                return callback.call();
            } catch (InterruptedException e) {
                log.warn("Thread [{}] got interrupted during 24h timer", Thread.currentThread());
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
