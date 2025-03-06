package ru.effective.mobile.tasks_dashboard.dto;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private long id;
    private String text;
    private UserDto authorDto;
    private TaskDto taskDto;
}