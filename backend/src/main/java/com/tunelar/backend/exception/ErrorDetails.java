package com.tunelar.backend.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Provides details on errors.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    /**
     * the timestamp of the error
     */
    private LocalDateTime timeStamp;
    /**
     * the message of the error
     */
    private String        message;
    /**
     * the details of the error
     */
    private String        details;
}