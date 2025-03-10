package ru.effective.mobile.tasks_dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentInputDto {
//
//    private Long authorId;

    @NotBlank(message = "Текст комментария является обязательным полем")
    private String text;

//    @NotNull(message = "ID задачи является обязательным полем")
//    private Long taskId;
}