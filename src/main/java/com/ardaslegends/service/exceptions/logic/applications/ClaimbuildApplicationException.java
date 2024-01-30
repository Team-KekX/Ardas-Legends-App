package com.ardaslegends.service.exceptions.logic.applications;


import com.ardaslegends.service.exceptions.logic.LogicException;

public class ClaimbuildApplicationException extends LogicException {
    private static final String CLAIMBUILD_NAME_ENDS_WITH_STARTER_HAMLET = "The Claimbuild's name must not end with 'starter hamlet'";

    private static final String CLAIMBUILD_APPLICATION_WITH_NAME_ALREADY_EXISTS = "Another open claimbuild application with the name '%s' already exists!";
    private static final String CLAIMBUILD_WITH_NAME_ALREADY_EXISTS = "Another claimbuild with the name '%s' already exists!";
    private static final String BUILDERS_NOT_FOUND = "No records found of the following builders [%s]";


    public static ClaimbuildApplicationException nameEndsWithStarterHamlet() {return new ClaimbuildApplicationException(CLAIMBUILD_NAME_ENDS_WITH_STARTER_HAMLET);}
    public static ClaimbuildApplicationException claimbuildApplicationWithNameAlreadyExists(String name) {return new ClaimbuildApplicationException(CLAIMBUILD_APPLICATION_WITH_NAME_ALREADY_EXISTS.formatted(name));}
    public static ClaimbuildApplicationException claimbuildWithNameAlreadyExists(String name) {return new ClaimbuildApplicationException(CLAIMBUILD_WITH_NAME_ALREADY_EXISTS.formatted(name));}
    public static ClaimbuildApplicationException buildersNotFound(String buildersIds) { return new ClaimbuildApplicationException(BUILDERS_NOT_FOUND.formatted(buildersIds)); }


    private ClaimbuildApplicationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    private ClaimbuildApplicationException(String message) {
        super(message);
    }
}
