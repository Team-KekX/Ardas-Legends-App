package com.ardaslegends.service.time;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        log.debug("Testing if starting a 24h timer works");
        log.debug("Current Thread: {}", Thread.currentThread());
        val list = new ArrayList<Integer>();

        val result = timeFreezeService.start24hTimer(() -> list.add(1));

        log.debug("Thread state after starting timer: {}", result.state().name());
        assertThat(result.isDone()).isFalse();
        assertThat(result.state()).isEqualTo(Future.State.RUNNING);

        Thread.sleep(Duration.ofSeconds(7));

        log.debug("Thread state after waiting 24h: {}", result.state().name());
        assertThat(result.isDone()).isTrue();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(1);
        log.info("Test passed: ensureStart24hTimerWorks");
    }
}
