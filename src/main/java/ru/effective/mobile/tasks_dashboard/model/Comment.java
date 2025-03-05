package ru.effective.mobile.tasks_dashboard.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column (name = "id")
    private UUID id;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    //todo JSON props ?
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id")
    private Task task;

    //todo JSON props ?
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}