package ru.effective.mobile.tasks_dashboard.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column (name = "status")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column (name = "priority")
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    @OneToMany(mappedBy = "task", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dueDate;
}


