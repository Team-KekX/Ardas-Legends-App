package com.ardaslegends.service.exceptions.region;

import com.ardaslegends.service.exceptions.ServiceException;

public class RegionServiceException extends ServiceException {
    protected RegionServiceException(String message) {
        super(message);
    }

    private static final String NO_REGION_FOUND = "No Region with id '%s' found!";

    public static RegionServiceException noRegionFound(String regionId) { return new RegionServiceException(NO_REGION_FOUND.formatted(regionId)); }
}