package com.ardaslegends.presentation.exceptions;

import com.ardaslegends.repository.exceptions.RepositoryException;
import com.ardaslegends.repository.exceptions.NotFoundException;
import com.ardaslegends.service.exceptions.logic.LogicException;
import com.ardaslegends.service.exceptions.permission.PermissionException;
import com.querydsl.core.QueryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /*
    ----------- PERSISTENCE LAYER
     */
    @ExceptionHandler({RepositoryException.class, QueryException.class})
    public ResponseEntity<Object> handleDataAccessException(RepositoryException exception, WebRequest request) {
        return handleException(exception, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception, WebRequest request) {
        return handleException(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<Object> handleNullPointerException(NullPointerException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request);
    }

    /*
    ----------- SERVICE LAYER
     */

    @ExceptionHandler({LogicException.class})
    public ResponseEntity<Object> handleLogicException(LogicException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({PermissionException.class})
    public ResponseEntity<Object> handlePermissionException(PermissionException exception, WebRequest request) {
        return handleException(exception, HttpStatus.FORBIDDEN, request);
    }

    private ResponseEntity<Object> handleException(Exception exception, HttpStatus httpStatus, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                httpStatus, request);
    }
}
