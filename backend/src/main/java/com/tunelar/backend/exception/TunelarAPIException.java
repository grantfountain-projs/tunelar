package com.tunelar.backend.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Exception for Tunelar API calls.
 */
@Getter
public class TunelarAPIException extends RuntimeException {
    /**
     * the serial version UID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * the status
     */
    private final HttpStatus status;

    /**
     * Constructor with status and message.
     * 
     * @param status the HTTP status
     * @param message the error message
     */
    public TunelarAPIException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
    
    /**
     * Constructor with status, message, and cause.
     * 
     * @param status the HTTP status
     * @param message the error message
     * @param cause the cause of the exception
     */
    public TunelarAPIException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}