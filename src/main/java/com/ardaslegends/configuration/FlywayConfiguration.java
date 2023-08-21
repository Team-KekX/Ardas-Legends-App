package com.ardaslegends.configuration;

import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

    @Bean
    public FlywayAutoConfiguration autoConfiguration() {
        return new FlywayAutoConfiguration();
    }

}
