package ru.effective.mobile.tasks_dashboard.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class LoginRequest {

    @Schema(description = "Email пользователя.", example = "user@ya.ru")
    @NotBlank(message = "Email является обязательным полем!")
    @Email(message = "Некорректный формат email")
    private String email;

    @Schema(description = "Пароль пользователя.", example = "password123")
    @NotBlank(message = "Пароль является обязательным полем!")
    private String password;
}
