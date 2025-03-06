package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import ru.effective.mobile.tasks_dashboard.dto.CommentDto;
import ru.effective.mobile.tasks_dashboard.model.Comment;


@Mapper
public interface CommentMapper {
    Comment dtoToEntity(CommentDto dto);
    CommentDto entityToDto(Comment comment);
}
