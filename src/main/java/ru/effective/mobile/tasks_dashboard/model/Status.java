package ru.effective.mobile.tasks_dashboard.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import ru.effective.mobile.tasks_dashboard.exception.IllegalTaskStatusException;

public enum Status {
    WAITING, IN_PROGRESS, COMPLETED;

    @JsonCreator
    public static Status fromString(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalTaskStatusException("Статус не может быть пустым");
        }
        try {
            return Status.valueOf(value.toUpperCase());
        } catch (IllegalTaskStatusException e) {
            throw new IllegalTaskStatusException("Некорректный статус: " + value);
        }
    }
}