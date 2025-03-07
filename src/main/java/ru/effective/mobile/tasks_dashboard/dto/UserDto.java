package ru.effective.mobile.tasks_dashboard.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.effective.mobile.tasks_dashboard.model.Role;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Email
    private String email;
    @NotNull
    private String password;
    private String username;
}
