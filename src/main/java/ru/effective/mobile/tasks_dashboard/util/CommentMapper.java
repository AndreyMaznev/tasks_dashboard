package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import ru.effective.mobile.tasks_dashboard.dto.CommentInputDto;
import ru.effective.mobile.tasks_dashboard.dto.CommentOutputDto;
import ru.effective.mobile.tasks_dashboard.model.Comment;


@Mapper(uses = {UserMapper.class, TaskMapper.class})
public interface CommentMapper {

    // Преобразование CommentInputDto → Comment
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "author", source = "authorId", qualifiedByName = "mapAuthor")
//    @Mapping(target = "task", source = "taskId", qualifiedByName = "mapTask")
    Comment commentInputDtoToComment(CommentInputDto dto);

    // Преобразование Comment → CommentOutputDto
//    @Mapping(source = "id", target = "id")
//    @Mapping(source = "createdAt", target = "createdAt")
//    @Mapping(source = "author", target = "authorDto")
//    @Mapping(source = "task", target = "taskDto")
    CommentOutputDto commentToCommentOutputDto(Comment comment);

}
