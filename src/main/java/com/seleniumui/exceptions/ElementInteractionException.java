package com.seleniumui.exceptions;

public class ElementInteractionException extends RuntimeException {

    public ElementInteractionException(String message) {
        super(message);
    }

    public ElementInteractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElementInteractionException(Throwable cause) {
        super(cause);
    }
}
