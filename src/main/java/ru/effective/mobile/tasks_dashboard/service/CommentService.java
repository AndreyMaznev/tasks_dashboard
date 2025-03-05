package ru.effective.mobile.tasks_dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effective.mobile.tasks_dashboard.model.Comment;
import ru.effective.mobile.tasks_dashboard.repository.CommentRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getCommentsByTaskId(UUID taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }
}
