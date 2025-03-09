package ru.effective.mobile.tasks_dashboard.service.interfaces;

import ru.effective.mobile.tasks_dashboard.dto.CommentInputDto;
import ru.effective.mobile.tasks_dashboard.dto.CommentOutputDto;
import ru.effective.mobile.tasks_dashboard.model.User;

public interface CommentService {
    CommentOutputDto getCommentOutputDtoById(long id);

    CommentOutputDto createComment(Long taskId, CommentInputDto commentInputDto, User currentUser);
    CommentOutputDto updateComment(Long taskId, Long commentId, CommentInputDto commentInputDto, User currentUser);
    void deleteComment(Long taskId, Long commentId, User currentUser);
}
