package com.ardaslegends.configuration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class ArdaConfiguration {
    @Bean
    public ExecutorService asyncExecutorService() {
        var executor = Executors.newFixedThreadPool(3);
        log.info("Initialized asyncExecutorService");
        return executor;
    }
}
