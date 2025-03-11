package ru.effective.mobile.tasks_dashboard.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Email является обязательным полем")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль является обязательным полем")
    private String password;
}
