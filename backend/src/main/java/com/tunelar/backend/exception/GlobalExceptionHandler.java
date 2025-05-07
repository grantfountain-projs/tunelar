package com.tunelar.backend.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles global errors.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles API exceptions
     *
     * @param exception
     *            the exception to handle
     * @param webRequest
     *            the web request to handle
     * @return a response with the error details
     */
    @ExceptionHandler(value = TunelarAPIException.class)
    public ResponseEntity<ErrorDetails> handleAPIException(final TunelarAPIException exception,
            final WebRequest webRequest) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(),
                webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}