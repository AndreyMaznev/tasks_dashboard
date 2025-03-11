package ru.effective.mobile.tasks_dashboard.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email пользователя является обязательным полем")
    private String email;

    @NotBlank(message = "Пароль пользователя является обязательным полем")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    private String password;
}