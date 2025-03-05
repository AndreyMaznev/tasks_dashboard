package ru.effective.mobile.tasks_dashboard.dto;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private UUID id;
    private String text;
    private UUID authorId;
    private UUID taskId;
}