package com.ardaslegends.configuration.converter;

import com.ardaslegends.domain.ClaimBuildType;
import org.springframework.core.convert.converter.Converter;

public class ClaimbuildTypeEnumConverter implements Converter<String, ClaimBuildType> {
    @Override
    public ClaimBuildType convert(String source) {
        return ClaimBuildType.valueOf(source.toUpperCase());
    }
}
