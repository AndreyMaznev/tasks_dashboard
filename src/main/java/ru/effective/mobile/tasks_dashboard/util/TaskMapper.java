package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import ru.effective.mobile.tasks_dashboard.dto.TaskDto;
import ru.effective.mobile.tasks_dashboard.model.Task;

@Mapper
public interface TaskMapper {
    Task dtoToEntity(TaskDto dto);
    TaskDto entityToDto(Task task);
}
