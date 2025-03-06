package ru.effective.mobile.tasks_dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.effective.mobile.tasks_dashboard.dto.CommentDto;
import ru.effective.mobile.tasks_dashboard.dto.TaskDto;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.service.CommentService;
import ru.effective.mobile.tasks_dashboard.service.TaskService;


@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;

    @GetMapping
    @Operation(
            summary = "Получить все задачи с разбивкой на страницы.",
            description = "Возвращает список всех задач с пагинацией. Параметры page и size определяют номер страницы и количество элементов на странице."
    )
    @ApiResponse(responseCode = "200", description = "Успешно получены задачи.")
    public ResponseEntity<Page<TaskDto>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.getAllTasks(pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Создание новой задачи, только для ADMIN.",
            description = "Создает новую задачу. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponse(responseCode = "201", description = "Задача успешно создана.")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен. Требуется роль ADMIN.")
    public ResponseEntity<TaskDto> createTask(
            @RequestBody TaskDto taskDto) {
        User currentUser = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(taskDto, currentUser));
    }

    @PutMapping("/{taskId}")
    @Operation(
            summary = "Редактировать задачу, только для создателя.",
            description = "Обновляет данные задачи. Доступно только создателю задачи."
    )
    @ApiResponse(responseCode = "200", description = "Задача успешно обновлена.")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен. Только создатель может редактировать задачу.")
    @ApiResponse(responseCode = "404", description = "Задача не найдена.")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskDto taskDto) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(taskService.updateTask(taskId, taskDto, currentUser));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Удаление задачи, только для ADMIN.",
            description = "Удаляет задачу по её ID. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponse(responseCode = "204", description = "Задача успешно удалена.")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен. Требуется роль ADMIN.")
    @ApiResponse(responseCode = "404", description = "Задача не найдена.")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId) {
        User currentUser = getCurrentUser();
        taskService.deleteTask(taskId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/comments")
    @Operation(
            summary = "Создание нового комментария к задаче.",
            description = "Создает новый комментарий для указанной задачи. Доступно авторизованным пользователям."
    )
    @ApiResponse(responseCode = "201", description = "Комментарий успешно создан.")
    @ApiResponse(responseCode = "404", description = "Задача не найдена.")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long taskId,
            @RequestBody CommentDto commentDto) {
        User currentUser = getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(taskId, commentDto, currentUser));
    }

    @PutMapping("/{taskId}/comments/{commentId}")
    @Operation(
            summary = "Редактирование комментария, только для создателя.",
            description = "Обновляет текст комментария. Доступно только создателю комментария."
    )
    @ApiResponse(responseCode = "200", description = "Комментарий успешно обновлен.")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен. Только создатель может редактировать комментарий.")
    @ApiResponse(responseCode = "404", description = "Комментарий не найден.")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @RequestBody CommentDto commentDto) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(commentService.updateComment(taskId, commentId, commentDto, currentUser));
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    @Operation(
            summary = "Удаление комментария, только для создателя.",
            description = "Удаляет комментарий по его ID. Доступно только создателю комментария."
    )
    @ApiResponse(responseCode = "204", description = "Комментарий успешно удален.")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен. Только создатель может удалить комментарий.")
    @ApiResponse(responseCode = "404", description = "Комментарий не найден.")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId) {
        User currentUser = getCurrentUser();
        commentService.deleteComment(taskId, commentId, currentUser);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}