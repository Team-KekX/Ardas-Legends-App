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
    public ResponseEntity<Object> handleInternalServerErrorRequest(RepositoryException exception, WebRequest request) {
        return handleException(exception, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundRequest(Exception exception, WebRequest request) {
        return handleException(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class, LogicException.class, AuthException.class})
    public ResponseEntity<Object> handleBadRequest(Exception exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({PermissionException.class})
    public ResponseEntity<Object> handleForbiddenRequest(Exception exception, WebRequest request) {
        return handleException(exception, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler

    private ResponseEntity<Object> handleException(Exception exception, HttpStatus httpStatus, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                httpStatus, request);
    }
    private ResponseEntity<Object> handleException(Exception exception, Object body, HttpStatus httpStatus, WebRequest request) {
        return handleExceptionInternal(exception, body, new HttpHeaders(),
                httpStatus, request);
    }
}
