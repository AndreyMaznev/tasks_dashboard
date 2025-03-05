package ru.effective.mobile.tasks_dashboard.exception;


public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
