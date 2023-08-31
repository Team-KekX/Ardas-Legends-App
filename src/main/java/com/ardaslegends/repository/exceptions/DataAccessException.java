package com.ardaslegends.repository.exceptions;

public class DataAccessException extends RuntimeException{

    private static final String UNEXPECTED_DATABASE_ERROR = "Unexpected Database Error: %s";
    private static final String COULD_NOT_SAVE = "Could not save %s %s due to database problems (details: %s)!";
    private static final String COULD_NOT_DELETE = "Could not delete %s %s due to database problems (details: %s)!";

    protected DataAccessException(String message) {
        super(message);
    }

    protected DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public static DataAccessException unexpectedDatabaseError(Exception cause) {
        return new DataAccessException(UNEXPECTED_DATABASE_ERROR.formatted(cause.getMessage()), cause);
    }

    public static <T> DataAccessException couldNotSaveEntity(T entity, Exception cause) {
        return new DataAccessException(COULD_NOT_SAVE.formatted(entity.getClass().getSimpleName(), entity.toString(), cause.getMessage()), cause);
    }

    public static <T> DataAccessException couldNotDeleteEntity(T entity, Exception cause) {
        return new DataAccessException(COULD_NOT_DELETE.formatted(entity.getClass().getSimpleName(), entity.toString(), cause.getMessage()), cause);
    }
}
