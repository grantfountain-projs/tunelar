package com.tunelar.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when a resource is not found.
 */
@ResponseStatus ( value = HttpStatus.NOT_FOUND )
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * creates a ResourceNotFoundException
     *
     * @param message the message of the exception
     *            
     */
    public ResourceNotFoundException ( final String message ) {
        super( message );
    }
}
