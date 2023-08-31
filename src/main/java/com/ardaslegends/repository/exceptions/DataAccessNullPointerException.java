package com.ardaslegends.repository.exceptions;

public class DataAccessNullPointerException extends NullPointerException {

    private final static String METHOD_PARAMETER_NULL = "Cannot execute query, method parameter '%s'  of method '%s' is required but was null";
    private final static String SECURE_FIND_FUNCTION_NULL = "Cannot execute secureFind query, function parameter is required but was null";

    private DataAccessNullPointerException(String message) {
        super(message + " -> PLEASE REPORT TO STAFF");
    }

    public static DataAccessNullPointerException queryMethodParameterWasNull(String parameterName, String methodName) {
        return new DataAccessNullPointerException(METHOD_PARAMETER_NULL.formatted(parameterName, methodName));
    }

    public static DataAccessNullPointerException secureFindFunctionWasNull() {
        return new DataAccessNullPointerException(SECURE_FIND_FUNCTION_NULL);
    }
}
