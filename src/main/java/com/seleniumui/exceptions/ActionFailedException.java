package com.seleniumui.exceptions;

public class ActionFailedException extends RuntimeException {
    public ActionFailedException(String message) {
        super(message);
    }
}
