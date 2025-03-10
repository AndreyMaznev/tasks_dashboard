package ru.effective.mobile.tasks_dashboard.exception.util;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {

    private int statusCode;
    private String message;
    private List<ValidationError> errors;

}