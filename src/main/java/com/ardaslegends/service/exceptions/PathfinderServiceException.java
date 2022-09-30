package com.ardaslegends.service.exceptions;

public class PathfinderServiceException extends ServiceException{
    private static final String NO_PATH_FOUND = "Could not find a valid path from region '%s' to region '%s'!";
    private static final String ALREADY_IN_REGION = "Target is already in destination region!";

    public static PathfinderServiceException noPathFound(String region1, String region2) { return new PathfinderServiceException(NO_PATH_FOUND.formatted(region1, region2)); }
    public static PathfinderServiceException alreadyInRegion() { return new PathfinderServiceException(ALREADY_IN_REGION); }

    protected PathfinderServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected PathfinderServiceException(String message) {
        super(message);
    }
}
