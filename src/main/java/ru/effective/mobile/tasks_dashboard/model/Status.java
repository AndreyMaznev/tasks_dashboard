package ru.effective.mobile.tasks_dashboard.model;

public enum Status {
    WAITING, IN_PROGRESS, COMPLETED;

    public static Status fromString(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalStatusException("Статус не может быть пустым");
        }
        try {
            return Status.valueOf(value.toUpperCase());
        } catch (IllegalStatusException e) {
            throw new IllegalStatusException("Некорректный статус: " + value);
        }
    }
}