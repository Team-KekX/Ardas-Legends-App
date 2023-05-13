package com.ardaslegends.service.exceptions.applications;


import com.ardaslegends.domain.applications.ClaimbuildApplication;
import com.ardaslegends.service.exceptions.ServiceException;

public class ClaimbuildApplicationException extends ServiceException {

    private static final String CLAIMBUILD_APPLICATION_WITH_NAME_ALREADY_EXISTS = "Another open claimbuild application with the name '%s' already exists!";
    private static final String CLAIMBUILD_WITH_NAME_ALREADY_EXISTS = "Another claimbuild with the name '%s' already exists!";


    public static ClaimbuildApplicationException claibuildApplicationWithNameAlreadyExists(String name) {return new ClaimbuildApplicationException(CLAIMBUILD_APPLICATION_WITH_NAME_ALREADY_EXISTS.formatted(name));}
    public static ClaimbuildApplicationException claibuildWithNameAlreadyExists(String name) {return new ClaimbuildApplicationException(CLAIMBUILD_WITH_NAME_ALREADY_EXISTS.formatted(name));}


    private ClaimbuildApplicationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    private ClaimbuildApplicationException(String message) {
        super(message);
    }
}
