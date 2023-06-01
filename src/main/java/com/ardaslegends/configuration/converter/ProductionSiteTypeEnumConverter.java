package com.ardaslegends.configuration.converter;

import com.ardaslegends.domain.ProductionSiteType;
import org.springframework.core.convert.converter.Converter;

public class ProductionSiteTypeEnumConverter implements Converter<String, ProductionSiteType> {
    @Override
    public ProductionSiteType convert(String source) {
        return ProductionSiteType.valueOf(source.toUpperCase());
    }
}
