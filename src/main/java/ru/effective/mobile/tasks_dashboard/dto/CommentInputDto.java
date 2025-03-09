package ru.effective.mobile.tasks_dashboard.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.effective.mobile.tasks_dashboard.dto.base.CommentBaseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentInputDto extends CommentBaseDto {

    private Long authorId;

    @NotNull(message = "ID задачи является обязательным полем")
    private Long taskId;
}