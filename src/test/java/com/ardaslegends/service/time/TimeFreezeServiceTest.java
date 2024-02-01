package com.ardaslegends.service.time;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TimeFreezeServiceTest {

    private TimeFreezeService timeFreezeService;

    @Before
    public void setup() {
        timeFreezeService = new TimeFreezeService(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Test
    public void ensureStart24hTimerWorks() throws InterruptedException {
        log.debug("{}", Thread.currentThread());
        val result = timeFreezeService.start24hTimer();
        Thread.sleep(Duration.ofSeconds(7));
        log.debug(result.state().name());
        assertThat(result.isDone()).isTrue();

    }
}
