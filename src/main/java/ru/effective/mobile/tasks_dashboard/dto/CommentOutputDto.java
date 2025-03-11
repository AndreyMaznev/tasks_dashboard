package ru.effective.mobile.tasks_dashboard.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentOutputDto {

    @Schema(description = "Идентификатор комментария.", example = "1")
    private long id;

    @Schema(description = "Текст комментария.", example = "Задача завершена. Прошу проверить корректность исполнения.")
    private String text;

    @Schema(description = "Дата и время создания комментария.", example = "2023-10-01 12:34:56")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Автор комментария, DTO сущности User.")
    private UserOutputDto author;

    @JsonIgnore
    private TaskOutputDto taskOutputDto;
}