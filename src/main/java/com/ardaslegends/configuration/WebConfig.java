package com.ardaslegends.configuration;

import com.ardaslegends.configuration.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new RegionTypeEnumConverter());
        registry.addConverter(new ClaimbuildTypeEnumConverter());
        registry.addConverter(new ProductionSiteTypeEnumConverter());
        registry.addConverter(new SpecialBuildingEnumConverter());
        registry.addConverter(new ResourceTypeEnumConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }
}
