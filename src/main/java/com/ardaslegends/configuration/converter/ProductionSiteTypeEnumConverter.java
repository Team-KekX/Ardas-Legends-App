package com.ardaslegends.configuration.converter;

import com.ardaslegends.domain.ClaimBuildType;
import com.ardaslegends.domain.ProductionSiteType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class ProductionSiteTypeEnumConverter implements Converter<String, ProductionSiteType>,
        com.fasterxml.jackson.databind.util.Converter<String, ProductionSiteType> {
    @Override
    public ProductionSiteType convert(String source) {
        log.debug("Converting '{}' into ProductionSiteType", source);
        var prodType = ProductionSiteType.valueOf(source.replace(' ', '_').toUpperCase());
        log.debug("Converted '{}' into ProductionSiteType {}", source, prodType);
        return prodType;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(TypeFactory.class);
    }
}
