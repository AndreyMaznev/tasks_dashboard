package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import ru.effective.mobile.tasks_dashboard.dto.CommentDto;
import ru.effective.mobile.tasks_dashboard.model.Comment;


@Mapper
public interface CommentMapper {
    Comment commentDtoToComment(CommentDto dto);
    CommentDto commentToCommentDto(Comment comment);
}
