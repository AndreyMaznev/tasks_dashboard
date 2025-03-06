package ru.effective.mobile.tasks_dashboard.exception;

public class IllegalJwtTokenException extends RuntimeException {
    public IllegalJwtTokenException(String message) {
        super(message);
    }
}
