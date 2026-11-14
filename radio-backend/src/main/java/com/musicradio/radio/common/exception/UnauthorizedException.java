package com.musicradio.radio.common.exception;

/**
 * Exception thrown when authentication fails or when credentials or security tokens are invalid, expired, or absent.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
