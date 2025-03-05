package ru.effective.mobile.tasks_dashboard.controller;

import org.springframework.web.bind.annotation.*;
import ru.effective.mobile.tasks_dashboard.dto.CommentDto;
import ru.effective.mobile.tasks_dashboard.service.CommentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public CommentDto createComment(@RequestBody CommentDto commentDto) {
        //todo - mapper
        return commentService.createComment(commentDto);
    }

    @GetMapping("/task/{id}")
    public List<CommentDto> getCommentsByTaskId(@PathVariable UUID id) {
        return commentService.getCommentsByTaskId(id);
    }
}
