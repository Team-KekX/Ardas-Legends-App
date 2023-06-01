package com.ardaslegends.configuration.converter;

import com.ardaslegends.domain.RegionType;
import org.springframework.core.convert.converter.Converter;


public class RegionTypeEnumConverter implements Converter<String, RegionType> {
    @Override
    public RegionType convert(String source) {
        return RegionType.valueOf(source.toUpperCase());
    }
}
