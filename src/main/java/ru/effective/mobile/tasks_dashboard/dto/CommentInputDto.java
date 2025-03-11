package ru.effective.mobile.tasks_dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentInputDto {

    @Schema(description = "Текст комментария", example = "Чтобы к полудню задача была готова!")
    @NotBlank(message = "Текст комментария является обязательным полем.")
    private String text;
}