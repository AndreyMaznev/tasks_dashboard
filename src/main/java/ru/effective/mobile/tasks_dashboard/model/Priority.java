package ru.effective.mobile.tasks_dashboard.model;

public enum Priority {
    HIGH, MEDIUM, LOW;

    public static Priority fromString(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalPriorityException("Приоритет не может быть пустым");
        }
        try {
            return Priority.valueOf(value.toUpperCase());
        } catch (IllegalPriorityException e) {
            throw new IllegalPriorityException("Некорректный приоритет: " + value);
        }
    }
}