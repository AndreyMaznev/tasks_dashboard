package ru.effective.mobile.tasks_dashboard.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effective.mobile.tasks_dashboard.dto.CommentInputDto;
import ru.effective.mobile.tasks_dashboard.dto.CommentOutputDto;
import ru.effective.mobile.tasks_dashboard.exception.AccessRefusedException;
import ru.effective.mobile.tasks_dashboard.exception.CommentNotFoundException;
import ru.effective.mobile.tasks_dashboard.exception.CommentUpdateException;
import ru.effective.mobile.tasks_dashboard.model.Comment;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.Task;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.repository.CommentRepository;
import ru.effective.mobile.tasks_dashboard.service.interfaces.CommentService;
import ru.effective.mobile.tasks_dashboard.util.CommentMapper;

import java.time.LocalDateTime;
import java.util.Objects;

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


    @Cacheable(value = "comments", key = "#commentId")
    @Transactional (readOnly = true)
    public CommentOutputDto getCommentById(long commentId) {
        return commentMapper.commentToCommentOutputDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий с ID " + commentId + " не найден")));
    }

    @CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public CommentOutputDto createComment(Long taskId, CommentInputDto commentInputDto, User currentUser) {
        taskServiceImpl.checkTaskIsExists(taskId);
        Task task = taskServiceImpl.getTaskById(taskId);
        boolean isAdmin = isCurrentUserAdmin();
        if (!isAdmin && !task.getExecutor().equals(currentUser) ) {
            throw new AccessRefusedException("Создавать комментарии могут только исполнители задачи или администраторы");
        }

        Comment comment = commentMapper.commentInputDtoToComment(commentInputDto);
        comment.setTask(task);
        comment.setAuthor(currentUser);
        comment.setCreatedAt(LocalDateTime.now());

        return commentMapper.commentToCommentOutputDto(commentRepository.save(comment));
    }

    @CacheEvict(value = "comments", key = "#commentId")
    @Transactional
    public CommentOutputDto updateComment(Long taskId, Long commentId, CommentInputDto commentInputDto, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        boolean isAdmin = isCurrentUserAdmin();
        if (!comment.getAuthor().equals(currentUser) && !isAdmin) {
            throw new AccessRefusedException("Редактировать комментарий может только автор или админ");
        }
        if (!Objects.equals(comment.getTask().getId(), taskId)) {
            throw new CommentUpdateException("Ошибка при редактировании комментария (Некорректный ID)");
        }
        comment.setText(commentInputDto.getText());
        return commentMapper.commentToCommentOutputDto(commentRepository.save(comment));
    }

    @CacheEvict(value = "comments", key = "#commentId")
    @Transactional
    public void deleteComment(Long taskId, Long commentId, User currentUser) {
        boolean isAdmin = isCurrentUserAdmin();
        if (!isAdmin) {
            throw new AccessRefusedException("Удалить или редактировать комментарий может только админ или автор");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        //Разница между переданным id задачи и тем, который в БД.
        if (!Objects.equals(comment.getTask().getId(), taskId)) {
            throw new CommentUpdateException("Ошибка при редактировании комментария (Некорректный ID)");
        }
        commentRepository.delete(comment);
    }

    public boolean isCurrentUserAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}