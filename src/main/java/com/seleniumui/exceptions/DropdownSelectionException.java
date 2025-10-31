package com.seleniumui.exceptions;

public class DropdownSelectionException extends RuntimeException {
    public DropdownSelectionException(String message) {
        super(message);
    }

    public DropdownSelectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
