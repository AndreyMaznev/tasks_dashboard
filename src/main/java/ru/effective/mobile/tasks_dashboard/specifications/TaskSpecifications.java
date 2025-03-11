package ru.effective.mobile.tasks_dashboard.specifications;


import org.springframework.data.jpa.domain.Specification;
import ru.effective.mobile.tasks_dashboard.model.Priority;
import ru.effective.mobile.tasks_dashboard.model.Status;
import ru.effective.mobile.tasks_dashboard.model.Task;


public class TaskSpecifications {

    public static Specification<Task> hasStatus(Status status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(Priority priority) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Task> hasAuthorName(String authorName) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("author").get("email")), "%" + authorName.toLowerCase() + "%");
        };
    }

    public static Specification<Task> hasExecutorName(String executorName) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("executor").get("email")), "%" + executorName.toLowerCase() + "%");
        };
    }
}
