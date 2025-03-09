package ru.effective.mobile.tasks_dashboard.dto.base;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public abstract class TaskBaseDto {

    @NotBlank(message = "Поле заголовок является обязательным полем!")
    @Size(min = 3, max = 100, message = "Поле заголовок должно иметь размер между 3 и 100 символами.")
    private String title;

    @NotBlank(message = "Описание является обязательным полем!")
    @Size(max = 500, message = "Описание задачи не должно превышать 500 символов")
    private String description;

    @Pattern(regexp = "WAITING|IN_PROGRESS|COMPLETED",
            message = "Статус может быть одним из: WAITING, IN_PROGRESS или COMPLETED")
    private String status;

    @Pattern(regexp = "^(HIGH|MEDIUM|LOW)$",
            message = "Приоритет задачи должен быть одним из: HIGH, MEDIUM, LOW")
    private String priority;

    @Future(message = "Поле исполнить к дате должно быть будущей датой.")
    private LocalDateTime dueDate;
}