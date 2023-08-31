package com.ardaslegends.repository;

import com.ardaslegends.repository.exceptions.DataAccessNullPointerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDataAccess {

    protected void requireParameterNonNull(Object parameter, String paramName, String methodName) {
        log.trace("Checking DataAccess parameter '{}' of function '{}' for null (value is '{}')", paramName, methodName, parameter);
        if(parameter == null) {
            log.warn("DataAccess layer parameter '{}' of method '{}' was null!", paramName, methodName);
            throw DataAccessNullPointerException.queryMethodParameterWasNull(paramName, methodName);
        }
    }
}
