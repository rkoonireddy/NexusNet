package ch.nexusnet.usermanager.controller;

import ch.nexusnet.usermanager.aws.s3.exceptions.UnsupportedFileTypeException;
import ch.nexusnet.usermanager.service.exceptions.FileDoesNotExistException;
import ch.nexusnet.usermanager.service.exceptions.UserAlreadyExistsException;
import ch.nexusnet.usermanager.service.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String SERVICE_NOT_AVAILABLE = "Service not available.";

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        return getResponseEntity(ex, request, HttpStatus.BAD_REQUEST, "User Already Exists");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(UserNotFoundException ex, WebRequest request) {
        return getResponseEntity(ex, request, HttpStatus.NOT_FOUND, "Not Found");
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleUnsupportedFileTypeException(UnsupportedFileTypeException ex, WebRequest request) {
        return getResponseEntity(ex, request, HttpStatus.BAD_REQUEST, "Unsupported File Type");
    }

    @ExceptionHandler(FileDoesNotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleUnsupportedFileTypeException(FileDoesNotExistException ex, WebRequest request) {
        return getResponseEntity(ex, request, HttpStatus.BAD_REQUEST, "File Does not exist");
    }

    @ExceptionHandler(URISyntaxException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleURISyntaxException(URISyntaxException ex, WebRequest request) {
        return getResponseEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, SERVICE_NOT_AVAILABLE);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleIOException(IOException ex, WebRequest request) {
        return getResponseEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, SERVICE_NOT_AVAILABLE);
    }

    private ResponseEntity<Map<String, Object>> getResponseEntity(
            Exception ex,
            WebRequest request,
            HttpStatus status,
            String error
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", status.value());
        body.put("errors", ex.getMessage());
        body.put("error", error);
        body.put("message", ex.getMessage());
        body.put("path", request.getContextPath());
        return new ResponseEntity<>(body, status);
    }

}
