package ru.effective.mobile.tasks_dashboard.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Schema(description = "Email пользователя.", example = "user@ya.ru")
    @Email(message = "Некорректный формат email.")
    @NotBlank(message = "Email пользователя является обязательным полем")
    private String email;

    @Schema(description = "Пароль пользователя.", example = "password123")
    @NotBlank(message = "Пароль пользователя является обязательным полем!")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    private String password;
}