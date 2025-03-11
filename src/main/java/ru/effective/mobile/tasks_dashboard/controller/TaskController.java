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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.effective.mobile.tasks_dashboard.dto.*;
import ru.effective.mobile.tasks_dashboard.dto.TaskInputDto;
import ru.effective.mobile.tasks_dashboard.dto.TaskOutputDto;
import ru.effective.mobile.tasks_dashboard.model.Priority;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.Status;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.service.implementations.CommentServiceImpl;
import ru.effective.mobile.tasks_dashboard.service.implementations.TaskServiceImpl;
import ru.effective.mobile.tasks_dashboard.service.implementations.UserServiceImpl;

import java.util.Set;


@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskServiceImpl taskServiceImpl;
    private final CommentServiceImpl commentServiceImpl;
    private final UserServiceImpl userServiceImpl;

    @GetMapping
    @Operation(
            summary = "Получить все задачи с разбивкой на страницы.",
            description = "Возвращает список всех задач с пагинацией. Параметры page и size определяют номер страницы и количество элементов на странице."
    )
    @ApiResponse(responseCode = "200", description = "Успешно получены задачи.")
    public ResponseEntity<Page<TaskOutputDto>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) String executorName) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskServiceImpl.getAllTasksWithFilters(status, priority, authorName, executorName, pageable));
    }

    @PostMapping
    @Operation(
            summary = "Создание новой задачи, только для ADMIN.",
            description = "Создает новую задачу. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponse(responseCode = "201", description = "Задача успешно создана.")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен. Требуется роль ADMIN.")
    public ResponseEntity<TaskOutputDto> createTask(
            @RequestBody TaskInputDto taskInputDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskServiceImpl.createTask(taskInputDto, getCurrentUser()));
    }

    @PutMapping("/{taskId}")
    @Operation(
            summary = "Редактировать задачу, только для создателя.",
            description = "Обновляет данные задачи. Доступно только создателю задачи."
    )
    @ApiResponse(responseCode = "200", description = "Задача успешно обновлена.")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен. Только создатель может редактировать задачу.")
    @ApiResponse(responseCode = "404", description = "Задача не найдена.")
    public ResponseEntity<TaskOutputDto> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskInputDto taskInputDto) {
        return ResponseEntity.ok(taskServiceImpl.updateTask(taskId, taskInputDto, getCurrentUser()));
    }

    @DeleteMapping("/{taskId}")
    @Operation(
            summary = "Удаление задачи, только для ADMIN.",
            description = "Удаляет задачу по её ID. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponse(responseCode = "204", description = "Задача успешно удалена.")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен. Требуется роль ADMIN.")
    @ApiResponse(responseCode = "404", description = "Задача не найдена.")
    public ResponseEntity<Void> deleteTask(
            @PathVariable long taskId) {
        taskServiceImpl.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/comments")
    @Operation(
            summary = "Создание нового комментария к задаче.",
            description = "Создает новый комментарий для указанной задачи. Доступно авторизованным пользователям."
    )
    @ApiResponse(responseCode = "201", description = "Комментарий успешно создан.")
    @ApiResponse(responseCode = "404", description = "Задача не найдена.")
    public ResponseEntity<CommentOutputDto> addComment(
            @PathVariable Long taskId,
            @RequestBody CommentInputDto commentInputDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentServiceImpl.createComment(taskId, commentInputDto, getCurrentUser()));
    }

    @PutMapping("/{taskId}/comrrrments/{commentId}")
    @Operation(
            summary = "Редактирование комментария, только для администратора.",
            description = "Обновляет текст комментария. Доступно только администратору."
    )
    @ApiResponse(responseCode = "200", description = "Комментарий успешно обновлен.")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен. Только создатель может редактировать комментарий.")
    @ApiResponse(responseCode = "404", description = "Комментарий не найден.")
    public ResponseEntity<CommentOutputDto> updateComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @RequestBody CommentInputDto commentInputDto) {
        return ResponseEntity.ok(commentServiceImpl.updateComment(taskId, commentId, commentInputDto, getCurrentUser()));
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    @Operation(
            summary = "Удаление комментария, только для администраторов.",
            description = "Удаляет комментарий по его ID. Доступно только администратору."
    )

    @ApiResponse(responseCode = "204", description = "Комментарий успешно удален.")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен. Только создатель может удалить комментарий.")
    @ApiResponse(responseCode = "404", description = "Комментарий не найден.")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId) {
        commentServiceImpl.deleteComment(taskId, commentId, getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userServiceImpl.getUserByEmail(userEmail);
    }
}