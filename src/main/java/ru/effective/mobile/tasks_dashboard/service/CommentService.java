package ru.effective.mobile.tasks_dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.effective.mobile.tasks_dashboard.dto.CommentDto;
import ru.effective.mobile.tasks_dashboard.exception.CommentNotFoundException;
import ru.effective.mobile.tasks_dashboard.exception.CommentUpdateException;
import ru.effective.mobile.tasks_dashboard.model.Comment;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.repository.CommentRepository;
import ru.effective.mobile.tasks_dashboard.util.CommentMapper;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TaskService taskService;


    @Autowired
    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper, TaskService taskService) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.taskService = taskService;
    }

    public CommentDto createComment(Long taskId, CommentDto commentDto, User currentUser) {
        taskService.checkTaskIsExists(taskId);
        Comment comment = commentMapper.commentDtoToComment(commentDto);
        comment.setTask(taskService.getTaskById(taskId));
        comment.setAuthor(currentUser);
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    public CommentDto updateComment(Long taskId, Long commentId, CommentDto commentDto, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден."));
        if (!comment.getAuthor().equals(currentUser)) {
            throw new AccessDeniedException("Редактировать комментарий может только автор.");
        }
        if (comment.getTask().getId()!=taskId) {
            throw new CommentUpdateException("Ошибка при редактировании комментария (Некорректный ID).");
        }
        comment.setText(commentDto.getText());
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    public void deleteComment(Long taskId, Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден."));
        if (comment.getTask().getId()!=taskId) {
            throw new CommentUpdateException("Ошибка при редактировании комментария (Некорректный ID).");
        }
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role == Role.ROLE_ADMIN);
        if (!isAdmin && !comment.getAuthor().equals(currentUser)) {
            throw new AccessDeniedException("Удалить или редактировать комментарий может только админ или автор.");
        }
        commentRepository.delete(comment);
    }
}