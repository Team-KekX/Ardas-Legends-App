package com.ardaslegends.configuration.converter;

import com.ardaslegends.domain.ProductionSiteType;
import com.ardaslegends.domain.SpecialBuilding;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class SpecialBuildingEnumConverter implements Converter<String, SpecialBuilding>,
        com.fasterxml.jackson.databind.util.Converter<String, SpecialBuilding> {
    @Override
    public SpecialBuilding convert(String source) {
        log.debug("Converting '{}' into SpecialBuilding", source);
        var specialBuilding = SpecialBuilding.valueOf(source.replace(' ', '_').toUpperCase());
        log.debug("Converted '{}' into SpecialBuilding {}", source, specialBuilding);
        return specialBuilding;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(SpecialBuilding.class);
    }
}
