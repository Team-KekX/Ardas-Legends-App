package com.ardaslegends.repository.exceptions;

public class RepositoryNullPointerException extends NullPointerException {

    private final static String METHOD_PARAMETER_NULL = "Cannot execute query, method parameter '%s'  of method '%s' is required but was null";
    private final static String SECURE_FIND_FUNCTION_NULL = "Cannot execute secureFind query, function parameter is required but was null";

    private RepositoryNullPointerException(String message) {
        super(message + " -> PLEASE REPORT TO STAFF");
    }

    public static RepositoryNullPointerException queryMethodParameterWasNull(String parameterName, String methodName) {
        return new RepositoryNullPointerException(METHOD_PARAMETER_NULL.formatted(parameterName, methodName));
    }

    public static RepositoryNullPointerException secureFindFunctionWasNull() {
        return new RepositoryNullPointerException(SECURE_FIND_FUNCTION_NULL);
    }
}
