package ru.effective.mobile.tasks_dashboard.exception;

public class AccessRefusedException extends RuntimeException {
    public AccessRefusedException(String message) {
        super(message);
    }
}
