package ru.effective.mobile.tasks_dashboard.model;

import ru.effective.mobile.tasks_dashboard.exception.IllegalTaskPriorityException;


public enum Priority {
    HIGH, MEDIUM, LOW;

    public static Priority fromString(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalTaskPriorityException("Приоритет не может быть пустым");
        }
        try {
            return Priority.valueOf(value.toUpperCase());
        } catch (IllegalTaskPriorityException e) {
            throw new IllegalTaskPriorityException("Некорректный приоритет: " + value);
        }
    }
}