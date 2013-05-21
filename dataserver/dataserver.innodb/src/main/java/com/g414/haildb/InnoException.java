package com.g414.haildb;

public class InnoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InnoException() {
        super();
    }

    public InnoException(String message, Throwable cause) {
        super(message, cause);
    }

    public InnoException(String message) {
        super(message);
    }

    public InnoException(Throwable cause) {
        super(cause);
    }
}
