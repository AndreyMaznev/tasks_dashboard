package ru.effective.mobile.tasks_dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskOutputDto {

    @Schema(description = "Идентификатор задачи.", example = "1")
    private long id;

    @Schema(description = "Заголовок задачи.", example = "Устранить баг по заявке №##### из Jira.")
    private String title;

    @Schema(description = "Описание задачи.", example = "Некорректно отображаются данные после вызова эндпоинта API ...")
    private String description;

    @Schema(description = "Статус задачи.", example = "WAITING")
    private String status;

    @Schema(description = "Приоритет задачи.", example = "HIGH")
    private String priority;

    @Schema(description = "Дата и время дедлайна задачи.", example = "2023-12-31 23:59")
    private LocalDateTime dueDate;

    @Schema(description = "Дата и время создания задачи.", example = "2023-10-01 12:34:56")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Дата и время последнего обновления задачи.", example = "2023-10-02 15:45:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "Список оставленных комментариев к задаче.")
    private List<CommentOutputDto> comments;

    @Schema(description = "Автор задачи.")
    private UserOutputDto author;

    @Schema(description = "Исполнитель задачи.")
    private UserOutputDto executor;
}