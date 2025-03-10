package ru.effective.mobile.tasks_dashboard.exception.util;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {

    private String field;
    private String message;
}