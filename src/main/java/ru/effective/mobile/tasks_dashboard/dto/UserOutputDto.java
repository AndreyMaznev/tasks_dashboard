package ru.effective.mobile.tasks_dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOutputDto{

    @Schema(description = "Идентификатор пользователя.", example = "1")
    private long id;

    @Schema(description = "Email пользователя.", example = "user@ya.ru")
    private String email;

    @Schema(description = "Имя пользователя.", example = "Anykeev")
    private String username;

    @Schema(description = "Роли пользователя, набор, одна или более.", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<String> roles;
}