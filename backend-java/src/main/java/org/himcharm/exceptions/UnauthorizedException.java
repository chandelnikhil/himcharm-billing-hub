package org.himcharm.exceptions;

/**
 * Thrown when a user attempts an action they are not authorized for. Mapped to HTTP 403.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}