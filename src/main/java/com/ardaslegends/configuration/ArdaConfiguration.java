package com.ardaslegends.configuration;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class ArdaConfiguration {
    @Bean
    public ExecutorService asyncExecutorService() {
        var executor = Executors.newFixedThreadPool(4);
        log.info("Initialized asyncExecutorService");
        return executor;
    }

    @Bean
    public UrlValidator urlValidator() {
        return new UrlValidator();
    }
}
