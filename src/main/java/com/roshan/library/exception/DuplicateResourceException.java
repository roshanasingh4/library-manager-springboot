package com.roshan.library.exception;

/**
 * Raised when a create or update would break a unique constraint
 * (for example a second author with the same e-mail address).
 * The field name lets the controller attach the message to the right form input.
 */
public class DuplicateResourceException extends RuntimeException {

    private final String field;

    public DuplicateResourceException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
