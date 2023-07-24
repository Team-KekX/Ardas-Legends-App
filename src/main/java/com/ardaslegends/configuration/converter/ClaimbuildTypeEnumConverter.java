package com.ardaslegends.configuration.converter;

import com.ardaslegends.domain.ClaimBuildType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
public class ClaimbuildTypeEnumConverter implements Converter<String, ClaimBuildType>,
        com.fasterxml.jackson.databind.util.Converter<String, ClaimBuildType> {
    @Override
    public ClaimBuildType convert(String source) {
        log.debug("Converting '{}' into ClaimBuildType...", source);
        var cbType = ClaimBuildType.valueOf(source.toUpperCase());
        log.debug("Converted '{}' into ClaimBuiltType {}", source, cbType);
        return cbType;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(ClaimBuildType.class);
    }
}
