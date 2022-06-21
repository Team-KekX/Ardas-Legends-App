package com.ardaslegends.data.service.exceptions;

import com.ardaslegends.data.domain.Region;

public class ServiceException extends RuntimeException {

    private static final String NO_REGIONS_TO_VISIT = "Encountered Error in pathfinder, no more regions to visit! Start region [%s], End region [%s]";

    public static ServiceException pathfinderNoRegions(Region startRegion, Region endRegion) {
        String msg = NO_REGIONS_TO_VISIT.formatted(startRegion,endRegion);
        return new ServiceException(msg);
    }

    private ServiceException(String message, Throwable rootCause) { super(message, rootCause);}
    private ServiceException(String message) { super(message);}

}
