package com.ardaslegends.repository.exceptions;

public class RepositoryException extends RuntimeException{

    private static final String UNEXPECTED_DATABASE_ERROR = "Unexpected Database Error: %s";
    private static final String COULD_NOT_SAVE = "Could not save %s %s due to database problems (details: %s)!";
    private static final String COULD_NOT_DELETE = "Could not delete %s %s due to database problems (details: %s)!";

    protected RepositoryException(String message) {
        super(message);
    }

    protected RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public static RepositoryException unexpectedDatabaseError(Exception cause) {
        return new RepositoryException(UNEXPECTED_DATABASE_ERROR.formatted(cause.getMessage()), cause);
    }

    public static <T> RepositoryException couldNotSaveEntity(T entity, Exception cause) {
        return new RepositoryException(COULD_NOT_SAVE.formatted(entity.getClass().getSimpleName(), entity.toString(), cause.getMessage()), cause);
    }

    public static <T> RepositoryException couldNotDeleteEntity(T entity, Exception cause) {
        return new RepositoryException(COULD_NOT_DELETE.formatted(entity.getClass().getSimpleName(), entity.toString(), cause.getMessage()), cause);
    }
}
