package ru.effective.mobile.tasks_dashboard.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.effective.mobile.tasks_dashboard.dto.CommentInputDto;
import ru.effective.mobile.tasks_dashboard.dto.CommentOutputDto;
import ru.effective.mobile.tasks_dashboard.exception.CommentNotFoundException;
import ru.effective.mobile.tasks_dashboard.exception.CommentUpdateException;
import ru.effective.mobile.tasks_dashboard.model.Comment;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.repository.CommentRepository;
import ru.effective.mobile.tasks_dashboard.service.interfaces.CommentService;
import ru.effective.mobile.tasks_dashboard.util.CommentMapper;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TaskServiceImpl taskServiceImpl;


    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper, TaskServiceImpl taskServiceImpl) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.taskServiceImpl = taskServiceImpl;
    }


    public CommentOutputDto getCommentOutputDtoById(long commentId) {
        return commentMapper.commentToCommentOutputDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий с ID " + commentId + " не найден")));
    }

    public CommentOutputDto createComment(Long taskId, CommentInputDto commentInputDto, User currentUser) {
        taskServiceImpl.checkTaskIsExists(taskId);
        Comment comment = commentMapper.commentInputDtoToComment(commentInputDto);
        comment.setTask(taskServiceImpl.getTaskById(taskId));
        comment.setAuthor(currentUser);
        return commentMapper.commentToCommentOutputDto(commentRepository.save(comment));
    }

    public CommentOutputDto updateComment(Long taskId, Long commentId, CommentInputDto commentInputDto, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден."));
        if (!comment.getAuthor().equals(currentUser)) {
            throw new AccessDeniedException("Редактировать комментарий может только автор.");
        }
        if (comment.getTask().getId()!=taskId) {
            throw new CommentUpdateException("Ошибка при редактировании комментария (Некорректный ID).");
        }
        comment.setText(commentInputDto.getText());
        return commentMapper.commentToCommentOutputDto(commentRepository.save(comment));
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