package ru.effective.mobile.tasks_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.effective.mobile.tasks_dashboard.model.Task;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    //todo позже добавить методы для расширенной фильтрации (если нужно)
    List<Task> findByAuthorId(UUID authorId);
    List<Task> findByExecutorId(UUID executorId);
}
