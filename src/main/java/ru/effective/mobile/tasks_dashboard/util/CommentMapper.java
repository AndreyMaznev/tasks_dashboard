package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.effective.mobile.tasks_dashboard.dto.CommentInputDto;
import ru.effective.mobile.tasks_dashboard.dto.CommentOutputDto;
import ru.effective.mobile.tasks_dashboard.model.Comment;


@Mapper(uses = {UserMapper.class, TaskMapper.class})
public interface CommentMapper {

    Comment commentInputDtoToComment(CommentInputDto dto);

    @Mapping(source = "comment.text", target = "text")
    @Mapping(source = "comment.author", target = "author")
    @Mapping(source = "comment.createdAt", target = "createdAt")
    CommentOutputDto commentToCommentOutputDto(Comment comment);
}
