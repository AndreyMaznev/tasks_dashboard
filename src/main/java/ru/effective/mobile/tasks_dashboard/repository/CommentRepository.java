package ru.effective.mobile.tasks_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.effective.mobile.tasks_dashboard.model.Comment;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskId(Long taskId);

//    @Modifying
//    @Query("DELETE FROM Comment c WHERE c.id = :commentId")
//    void deleteById(@Param("commentId") Long commentId);
}
