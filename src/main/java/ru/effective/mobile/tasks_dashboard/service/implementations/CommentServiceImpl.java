package ru.effective.mobile.tasks_dashboard.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effective.mobile.tasks_dashboard.dto.CommentInputDto;
import ru.effective.mobile.tasks_dashboard.dto.CommentOutputDto;
import ru.effective.mobile.tasks_dashboard.exception.AccessRefusedException;
import ru.effective.mobile.tasks_dashboard.exception.CommentNotFoundException;
import ru.effective.mobile.tasks_dashboard.exception.CommentUpdateException;
import ru.effective.mobile.tasks_dashboard.model.*;
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

    @CachePut(value = "comments", key = "#result.id")
    @Transactional
    public CommentOutputDto createComment(Long taskId, CommentInputDto commentInputDto, User currentUser) {
        Task task = taskServiceImpl.getTaskById(taskId);
        boolean isAdmin = isCurrentUserAdmin();
        if (!isAdmin && task.getExecutor() == null) {
            throw new AccessRefusedException("Оставлять комментарии может только исполнитель или администратор");
        }
        if (!isAdmin && !task.getExecutor().equals(currentUser)) {
            throw new AccessRefusedException("Оставлять комментарии может только исполнитель или администратор");
        }
        Comment comment = commentMapper.commentInputDtoToComment(commentInputDto);
        comment.setTask(task);
        comment.setAuthor(currentUser);
        comment.setCreatedAt(LocalDateTime.now());

        return commentMapper.commentToCommentOutputDto(commentRepository.save(comment));
    }

    @CachePut(value = "comments", key = "#commentId")
    @Transactional
    public CommentOutputDto updateComment(Long taskId, Long commentId, CommentInputDto commentInputDto, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        if (!isCurrentUserAdmin()) {
            throw new AccessRefusedException("Редактировать комментарий может только админ");
        }
        if (!Objects.equals(comment.getTask().getId(), taskId)) {
            throw new CommentUpdateException("Ошибка при редактировании комментария: Некорректные значения ID");
        }
        comment.setText(commentInputDto.getText());
        return commentMapper.commentToCommentOutputDto(commentRepository.save(comment));
    }

    @CacheEvict(value = "comments", key = "#commentId")
    @Transactional
    public void deleteComment(Long taskId, Long commentId, User currentUser) {
        boolean isAdmin = isCurrentUserAdmin();
        if (!isAdmin) {
            throw new AccessRefusedException("Удалить комментарий может только админ");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        //Если разница между переданным id задачи и тем, который в БД.
        if (!Objects.equals(comment.getTask().getId(), taskId)) {
            throw new CommentUpdateException("Ошибка при редактировании комментария: Некорректные значения ID");
        }
        commentRepository.delete(comment);
    }

    public boolean isCurrentUserAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}