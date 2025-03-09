package ru.effective.mobile.tasks_dashboard.dto.base;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public abstract class CommentBaseDto {

    @NotBlank(message = "Текст комментария является обязательным полем")
    private String text;
}