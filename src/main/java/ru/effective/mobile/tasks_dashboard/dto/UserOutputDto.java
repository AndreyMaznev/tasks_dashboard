package ru.effective.mobile.tasks_dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOutputDto{

    private long id;

    private String email;

    private String username;

//    private Set<String> roles;
}