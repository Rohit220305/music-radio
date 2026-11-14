package com.musicradio.radio.common.exception;

/**
 * Thrown when an incoming request fails business validation rules.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}

