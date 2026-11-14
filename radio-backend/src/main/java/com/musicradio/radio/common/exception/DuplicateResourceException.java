package com.musicradio.radio.common.exception;

/**
 * Thrown when an attempt is made to create or update a resource with a conflicting unique field (e.g. slug, email).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}

