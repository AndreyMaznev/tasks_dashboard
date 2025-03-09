package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.effective.mobile.tasks_dashboard.dto.TaskInputDto;
import ru.effective.mobile.tasks_dashboard.dto.TaskOutputDto;
import ru.effective.mobile.tasks_dashboard.model.Task;

@Mapper(uses = {UserMapper.class})
public interface TaskMapper {

    //TaskInputDto → Task
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task taskInputDtoToTask(TaskInputDto dto);

    //Task → TaskOutputDto
    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    TaskOutputDto taskToTaskOutputDto(Task task);
}
