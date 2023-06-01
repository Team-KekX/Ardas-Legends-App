package com.ardaslegends.configuration.converter;

import com.ardaslegends.domain.SpecialBuilding;
import org.springframework.core.convert.converter.Converter;

public class SpecialBuildingEnumConverter implements Converter<String, SpecialBuilding> {
    @Override
    public SpecialBuilding convert(String source) {
        return SpecialBuilding.valueOf(source.toUpperCase());
    }
}
