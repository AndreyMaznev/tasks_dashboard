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

    @NotBlank(message = "Текст комментария является обязательным полем")
    private String text;
}