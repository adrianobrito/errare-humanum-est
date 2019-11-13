package com.github.adrianobrito;

import static java.lang.String.format;

public class UnhandledExceptionError extends RuntimeException {

    private static final String MESSAGE = "An exception was not properly handled: ";

    public UnhandledExceptionError(Exception cause) {
        super(format("%s : %s", MESSAGE, cause), cause);
    }

}
