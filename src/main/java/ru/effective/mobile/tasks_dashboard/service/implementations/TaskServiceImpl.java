package ru.effective.mobile.tasks_dashboard.service.implementations;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effective.mobile.tasks_dashboard.dto.TaskInputDto;
import ru.effective.mobile.tasks_dashboard.dto.TaskOutputDto;
import ru.effective.mobile.tasks_dashboard.exception.AccessRefusedException;
import ru.effective.mobile.tasks_dashboard.exception.TaskNotFoundException;
import ru.effective.mobile.tasks_dashboard.model.*;
import ru.effective.mobile.tasks_dashboard.repository.TaskRepository;
import ru.effective.mobile.tasks_dashboard.service.interfaces.TaskService;
import ru.effective.mobile.tasks_dashboard.specifications.TaskSpecifications;
import ru.effective.mobile.tasks_dashboard.util.TaskMapper;
import ru.effective.mobile.tasks_dashboard.util.UserMapper;

import java.time.LocalDateTime;


@Service
public class TaskServiceImpl implements TaskService {
    private final UserServiceImpl userServiceImpl;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;


    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, UserServiceImpl userServiceImpl, UserMapper userMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userServiceImpl = userServiceImpl;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public Page<TaskOutputDto> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(taskMapper::taskToTaskOutputDto);
    }

    @Transactional(readOnly = true)
    public Page<TaskOutputDto> getAllTasksWithFilters(
            Status status,
            Priority priority,
            String authorName,
            String executorName,
            Pageable pageable
    ) {
        Specification<Task> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and(TaskSpecifications.hasStatus(status));
        }
        if (priority != null) {
            spec = spec.and(TaskSpecifications.hasPriority(priority));
        }
        if (authorName != null && !authorName.isEmpty()) {
            spec = spec.and(TaskSpecifications.hasAuthorName(authorName));
        }
        if (executorName != null && !executorName.isEmpty()) {
            spec = spec.and(TaskSpecifications.hasExecutorName(executorName));
        }

        return taskRepository.findAll(spec, pageable).map(taskMapper::taskToTaskOutputDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tasks", key = "#taskId")
    public Task getTaskById(long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
    }

    public TaskOutputDto getTaskOutputDtoById(long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
        return taskMapper.taskToTaskOutputDto(task);
    }

    @Transactional
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskOutputDto createTask(TaskInputDto taskInputDto, User currentUser) {
        if (!isCurrentUserAdmin()) {
            throw new AccessRefusedException("Создавать задачи могут только администраторы.");
        }
        Task task = taskMapper.taskInputDtoToTask(taskInputDto);
        task.setAuthor(currentUser);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        if (taskInputDto.getExecutor() != null) {
            task.setExecutor(userServiceImpl.getUserByEmail(taskInputDto.getExecutor().getEmail()));
        } else {
            task.setExecutor(null);
        }

        return taskMapper.taskToTaskOutputDto(taskRepository.save(task));
    }

    @Transactional
    @CacheEvict(value = "tasks", key = "#taskId")
    public TaskOutputDto updateTask(Long taskId, TaskInputDto taskInputDto, User currentUser) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
        boolean isAdmin = isCurrentUserAdmin();
        if (!task.getExecutor().equals(currentUser) && !isAdmin) {
            throw new AccessRefusedException("Редактировать задачу может только исполнитель или администратор");
        }

        if (task.getExecutor().equals(currentUser) || !isCurrentUserAdmin()) {
            task.setStatus(Status.fromString(taskInputDto.getStatus()));
        } else if (isCurrentUserAdmin()) {
            task.setTitle(taskInputDto.getTitle());
            task.setDescription(taskInputDto.getDescription());
            task.setPriority(Priority.fromString(taskInputDto.getPriority()));
            task.setUpdatedAt(LocalDateTime.now());
            task.setDueDate(taskInputDto.getDueDate());
            if (taskInputDto.getExecutor() != null) {
                task.setExecutor(userMapper.userInputDtoToUser(taskInputDto.getExecutor()));
            }
        }
        return taskMapper.taskToTaskOutputDto(taskRepository.save(task));
    }

    @Transactional
    @CacheEvict(value = "tasks", key = "#taskId")
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Задача с ID " + taskId + " не найдена"));
        if (!isCurrentUserAdmin()) {
            throw new AccessRefusedException("Удалять задачи могут только администраторы.");
        }
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public void checkTaskIsExists(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("Задача с ID " + taskId + " не найдена");
        }
    }

    public boolean isCurrentUserAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}