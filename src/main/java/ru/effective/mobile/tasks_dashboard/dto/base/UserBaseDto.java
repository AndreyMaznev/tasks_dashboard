package ru.effective.mobile.tasks_dashboard.dto.base;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public abstract class UserBaseDto {

    @Email(message = "Некорректный email")
    private String email;

    @NotNull(message = "Пароль является обязательным полем")
    private String password;

    private String username;
}