package ru.effective.mobile.tasks_dashboard.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effective.mobile.tasks_dashboard.dto.TaskDto;
import ru.effective.mobile.tasks_dashboard.exception.TaskNotFoundException;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.Task;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.repository.TaskRepository;
import ru.effective.mobile.tasks_dashboard.util.TaskMapper;


@Service
public class TaskService {
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, UserService userService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userService = userService;
    }

    public Page<TaskDto> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(taskMapper::entityToDto);
    }

    public Task getTaskById(long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
    }

    public TaskDto getTaskDtoById(long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
        return taskMapper.entityToDto(task);
    }

    public TaskDto createTask(TaskDto taskDto, User currentUser) {
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role == Role.ROLE_ADMIN);
        if (!isAdmin) {
            throw new AccessDeniedException("Создавать задачи могут только администраторы.");
        }
        Task task = taskMapper.dtoToEntity(taskDto);
        task.setAuthor(currentUser);
        return taskMapper.entityToDto(taskRepository.save(task));
    }

    public TaskDto updateTask(Long taskId, TaskDto taskDto, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
        if (!task.getAuthor().equals(currentUser)) {
            throw new AccessDeniedException("Редактировать задачу может только автор.");
        }
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        User executor = userService.getUserByEmail(taskDto.getExecutor().getEmail());
        task.setExecutor(executor);
        return taskMapper.entityToDto(taskRepository.save(task));
    }

    public void deleteTask(Long taskId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role == Role.ROLE_ADMIN);
        if (!isAdmin) {
            throw new AccessDeniedException("Создавать задачи могут только администраторы.");
        }
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public void checkTaskIsExists(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("Задача с ID " + taskId + " не найдена");
        }
    }
}