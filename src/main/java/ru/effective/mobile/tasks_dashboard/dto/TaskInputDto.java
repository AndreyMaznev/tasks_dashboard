package ru.effective.mobile.tasks_dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskInputDto {

    @Schema(description = "Заголовок задачи.", example = "Устранить баг по заявке №##### из Jira.")
    @NotBlank(message = "Поле заголовок является обязательным полем!")
    @Size(min = 3, max = 100, message = "Поле заголовок должно иметь размер между 3 и 100 символами.")
    private String title;

    @Schema(description = "Описание задачи.", example = "Некорректно отображаются данные после вызова эндпоинта API ...")
    @NotBlank(message = "Описание является обязательным полем!")
    @Size(max = 500, message = "Описание задачи не должно превышать 500 символов")
    private String description;

    @Schema(description = "Статус задачи, только большими буквами.", example = "WAITING")
    @Pattern(regexp = "WAITING|IN_PROGRESS|COMPLETED",
            message = "Статус может быть одним из: WAITING, IN_PROGRESS или COMPLETED")
    private String status;

    @Schema(description = "Приоритет задачи, только большими буквами.", example = "HIGH")
    @Pattern(regexp = "^(HIGH|MEDIUM|LOW)$",
            message = "Приоритет задачи должен быть одним из: HIGH, MEDIUM, LOW")
    private String priority;

    @Schema(description = "Дата и время дедлайна задачи, только будущей датой.", example = "2023-12-31 23:59")
    @Future(message = "Поле исполнить к дате должно быть будущей датой.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dueDate;

    private UserInputDto executor;
}
