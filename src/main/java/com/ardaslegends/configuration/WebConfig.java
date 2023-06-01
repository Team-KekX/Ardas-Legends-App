package com.ardaslegends.configuration;

import com.ardaslegends.configuration.converter.ClaimbuildTypeEnumConverter;
import com.ardaslegends.configuration.converter.ProductionSiteTypeEnumConverter;
import com.ardaslegends.configuration.converter.RegionTypeEnumConverter;
import com.ardaslegends.configuration.converter.SpecialBuildingEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new RegionTypeEnumConverter());
        registry.addConverter(new ClaimbuildTypeEnumConverter());
        registry.addConverter(new ProductionSiteTypeEnumConverter());
        registry.addConverter(new SpecialBuildingEnumConverter());
    }
}
