package com.ardaslegends.configuration.converter;

import com.ardaslegends.domain.ResourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class ResourceTypeEnumConverter implements Converter<String, ResourceType> {
    @Override
    public ResourceType convert(String source) {
        log.debug("Converting '{}' into ResourceType...", source);
        var resourceType = ResourceType.valueOf(source.toUpperCase());
        log.debug("Converted '{}' into ResourceType {}", source, resourceType);
        return resourceType;
    }
}
