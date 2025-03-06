package ru.effective.mobile.tasks_dashboard.dto;


import lombok.*;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    @NotBlank(message = "Поле заголовок является обязательным полем!")
    @Size(min = 3, max = 100, message = "Поле заголовок должно иметь размер между 3 и 100 символами.")
    private String title;

    @NotBlank(message = "Описание является обязательным полем!")
    @Size(max = 500, message = "Описание задачи не должно превышать 500 символов")
    private String description;

    /**
        Использую String, а не Enum для большей гибкости при добавлении новых статусов и меньшей связи с Frontend.
        В соответствии с ТЗ: WAITING, IN_PROGRESS, COMPLETED
     */
    @Pattern(regexp = "WAITING|IN_PROGRESS|COMPLETED",
            message = "Статус может быть одним из: WAITING, IN_PROGRESS или COMPLETED")
    private String status;

    /**
        Аналогично status - использую String, а не Enum для большей гибкости при добавлении новых приоритетов
        и меньшей связи с Frontend.
        В соответствии с ТЗ: HIGH, MEDIUM, LOW
    */
    @Pattern(regexp = "^(HIGH|MEDIUM|LOW)$",
            message = "Приоритет задачи должен быть одним из: HIGH, MEDIUM, LOW")
    private String priority;

    private UserDto author;
    private UserDto executor;

    @Future(message = "Поле исполнить к дате должно быть будущей датой.")
    private LocalDateTime dueDate;// Не требует @NotNull, так как может быть пустым
}
