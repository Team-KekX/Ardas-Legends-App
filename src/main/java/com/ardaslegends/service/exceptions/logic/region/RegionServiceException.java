package com.ardaslegends.service.exceptions.logic.region;

import com.ardaslegends.service.exceptions.logic.LogicException;

public class RegionServiceException extends LogicException {
    protected RegionServiceException(String message) {
        super(message);
    }

    private static final String NO_REGION_FOUND = "No Region with id '%s' found!";

    public static RegionServiceException noRegionFound(String regionId) { return new RegionServiceException(NO_REGION_FOUND.formatted(regionId)); }
}
