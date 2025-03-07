package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import ru.effective.mobile.tasks_dashboard.dto.TaskDto;
import ru.effective.mobile.tasks_dashboard.model.Task;

@Mapper
public interface TaskMapper {
    Task taskDtoToTask(TaskDto dto);
    TaskDto taskToTaskDto(Task task);
}
