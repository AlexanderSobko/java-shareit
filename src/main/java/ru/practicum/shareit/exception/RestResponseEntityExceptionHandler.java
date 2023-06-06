package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final HttpHeaders headers;

    static {
        headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
    }

    @ExceptionHandler(value = AlreadyExistsException.class)
    protected ResponseEntity<Object> handleAlreadyExistsExc(AlreadyExistsException ex, WebRequest request) {
        log.warn(ex.getMessage());
        return handleExceptionInternal(ex, Map.of("message", ex.getMessage()),
                headers, CONFLICT, request);
    }

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<Object> handleNotExistExc(NotFoundException ex, WebRequest request) {
        log.warn(ex.getMessage());
        return handleExceptionInternal(ex, Map.of("message", ex.getMessage()),
                headers, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception,
                                                                    WebRequest request) {
        return handleExceptionInternal(exception, Map.of("error", "Unknown state: UNSUPPORTED_STATUS")
                , this.headers, INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        List<String> errorList = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        log.warn(String.join("/n", errorList));
        return handleExceptionInternal(exception, errorList, RestResponseEntityExceptionHandler.headers, BAD_REQUEST, request);
    }
}
