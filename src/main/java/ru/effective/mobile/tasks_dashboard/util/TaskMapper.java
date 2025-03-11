package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.effective.mobile.tasks_dashboard.dto.TaskInputDto;
import ru.effective.mobile.tasks_dashboard.dto.TaskOutputDto;
import ru.effective.mobile.tasks_dashboard.model.Task;


@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface TaskMapper {

    @Mapping(source = "taskInputDto.executor", target = "executor")
    Task taskInputDtoToTask(TaskInputDto taskInputDto);


    @Mapping(source = "task.id", target = "id")
    @Mapping(source = "task.author", target = "author")
    @Mapping(source = "task.executor", target = "executor")
    @Mapping(source = "task.comments", target = "comments")
    TaskOutputDto taskToTaskOutputDto(Task task);

}
