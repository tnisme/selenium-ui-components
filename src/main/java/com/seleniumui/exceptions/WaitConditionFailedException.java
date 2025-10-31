package com.seleniumui.exceptions;

public class WaitConditionFailedException extends RuntimeException {
    public WaitConditionFailedException(String message) {
        super(message);
    }

    public WaitConditionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
