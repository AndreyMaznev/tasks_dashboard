package ru.effective.mobile.tasks_dashboard.service;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effective.mobile.tasks_dashboard.model.Task;
import ru.effective.mobile.tasks_dashboard.repository.TaskRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getTasksByAuthorId(UUID authorId) {
        return taskRepository.findByAuthorId(authorId);
    }

    public List<Task> getTasksByAssigneeId(UUID assigneeId) {
        return taskRepository.findByExecutorId(assigneeId);
    }

    public void deleteTaskById(UUID taskId) {
        taskRepository.deleteById(taskId);
    }
}