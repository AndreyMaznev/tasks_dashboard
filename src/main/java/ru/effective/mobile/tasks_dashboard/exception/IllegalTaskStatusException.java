package ru.effective.mobile.tasks_dashboard.exception;

public class IllegalTaskStatusException extends RuntimeException {
    public IllegalTaskStatusException(String message) {
        super(message);
    }
}
