package ru.effective.mobile.tasks_dashboard.service.interfaces;

import ru.effective.mobile.tasks_dashboard.dto.TaskInputDto;
import ru.effective.mobile.tasks_dashboard.dto.TaskOutputDto;
import ru.effective.mobile.tasks_dashboard.model.Task;
import ru.effective.mobile.tasks_dashboard.model.User;

public interface TaskService {
    Task getTaskById(long taskId);
    TaskOutputDto createTask(TaskInputDto taskInputDto, User currentUser);
    TaskOutputDto updateTask(Long taskId, TaskInputDto taskInputDto, User currentUser);
    void deleteTask(Long taskId);
}
