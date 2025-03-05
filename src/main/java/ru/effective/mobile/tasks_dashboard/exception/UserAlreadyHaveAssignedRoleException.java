package ru.effective.mobile.tasks_dashboard.exception;

public class UserAlreadyHaveAssignedRoleException extends RuntimeException {
    public UserAlreadyHaveAssignedRoleException(String message) {
        super(message);
    }
}

