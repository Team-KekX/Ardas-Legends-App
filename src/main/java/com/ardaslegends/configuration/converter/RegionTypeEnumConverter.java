package com.ardaslegends.configuration.converter;

import com.ardaslegends.domain.ClaimBuildType;
import com.ardaslegends.domain.RegionType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;


@Slf4j
public class RegionTypeEnumConverter implements Converter<String, RegionType>,
        com.fasterxml.jackson.databind.util.Converter<String, RegionType> {
    @Override
    public RegionType convert(String source) {
        log.debug("Converting '{}' into RegionType...", source);
        var regionType = RegionType.valueOf(source.toUpperCase());
        log.debug("Converted '{}' into RegionType {}", source, regionType);
        return regionType;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(RegionType.class);
    }
}
