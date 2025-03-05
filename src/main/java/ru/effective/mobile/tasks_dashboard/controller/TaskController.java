package ru.effective.mobile.tasks_dashboard.controller;

import org.springframework.web.bind.annotation.*;
import ru.effective.mobile.tasks_dashboard.dto.TaskDto;
import ru.effective.mobile.tasks_dashboard.service.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskDto createTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @GetMapping("/author/{id}")
    public List<TaskDto> getTasksByAuthorId(@PathVariable UUID id) {
        return taskService.getTasksByAuthorId(id);
    }

    @GetMapping("/assignee/{id}")
    public List<TaskDto> getTasksByAssigneeId(@PathVariable UUID id) {
        return taskService.getTasksByAssigneeId(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTaskById(@PathVariable UUID id) {
        taskService.deleteTaskById(id);
    }
}
