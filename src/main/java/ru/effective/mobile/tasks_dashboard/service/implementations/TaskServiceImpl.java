package ru.effective.mobile.tasks_dashboard.service.implementations;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effective.mobile.tasks_dashboard.dto.TaskInputDto;
import ru.effective.mobile.tasks_dashboard.dto.TaskOutputDto;
import ru.effective.mobile.tasks_dashboard.exception.TaskNotFoundException;
import ru.effective.mobile.tasks_dashboard.model.*;
import ru.effective.mobile.tasks_dashboard.repository.TaskRepository;
import ru.effective.mobile.tasks_dashboard.service.interfaces.TaskService;
import ru.effective.mobile.tasks_dashboard.util.TaskMapper;


@Service
public class TaskServiceImpl implements TaskService {
    private final UserServiceImpl userServiceImpl;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, UserServiceImpl userServiceImpl) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userServiceImpl = userServiceImpl;
    }

    public Page<TaskOutputDto> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(taskMapper::taskToTaskOutputDto);
    }

    public Task getTaskById(long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
    }

    public TaskOutputDto getTaskOutputDtoById(long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
        return taskMapper.taskToTaskOutputDto(task);
    }

    public TaskOutputDto createTask(TaskInputDto taskInputDto, User currentUser) {
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role == Role.ROLE_ADMIN);
        if (!isAdmin) {
            throw new AccessDeniedException("Создавать задачи могут только администраторы.");
        }
        Task task = taskMapper.taskInputDtoToTask(taskInputDto);
        task.setAuthor(currentUser);

        if (taskInputDto.getExecutor() != null) {
            task.setExecutor(userServiceImpl.getUserById(taskInputDto.getExecutor()));
        }
        return taskMapper.taskToTaskOutputDto(taskRepository.save(task));
    }

    public TaskOutputDto updateTask(Long taskId, TaskInputDto taskInputDto, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
        if (!task.getAuthor().equals(currentUser)) {
            throw new AccessDeniedException("Редактировать задачу может только автор.");
        }

        task.setTitle(taskInputDto.getTitle());
        task.setDescription(taskInputDto.getDescription());
        task.setDueDate(taskInputDto.getDueDate());
        task.setStatus(Status.fromString(taskInputDto.getStatus()));
        task.setPriority(Priority.fromString(taskInputDto.getPriority()));

        if (taskInputDto.getExecutor() != null) {
            task.setExecutor(userServiceImpl.getUserById(taskInputDto.getExecutor()));
        }

        return taskMapper.taskToTaskOutputDto(taskRepository.save(task));
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