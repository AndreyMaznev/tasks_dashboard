package ru.effective.mobile.tasks_dashboard.dto;


import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private UUID id;
    private String title;
    private String description;
    private String status; // по ТЗ: WAITING, IN_PROGRESS, COMPLETED
    private String priority; // по ТЗ: HIGH, MEDIUM, LOW
    private UUID authorId;
    private UUID assignedAt;
    private List<CommentDto> comments;
}