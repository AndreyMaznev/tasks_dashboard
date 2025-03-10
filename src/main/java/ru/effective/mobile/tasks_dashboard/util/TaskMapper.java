package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import ru.effective.mobile.tasks_dashboard.dto.TaskInputDto;
import ru.effective.mobile.tasks_dashboard.dto.TaskOutputDto;
import ru.effective.mobile.tasks_dashboard.model.Task;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TaskMapper {

    @Mapping(source = "taskInputDto.executor", target = "executor")
    Task taskInputDtoToTask(TaskInputDto taskInputDto);

    @Mapping(source = "task.author", target = "author")
    @Mapping(source = "task.executor", target = "executor")
    TaskOutputDto taskToTaskOutputDto(Task task);
}
