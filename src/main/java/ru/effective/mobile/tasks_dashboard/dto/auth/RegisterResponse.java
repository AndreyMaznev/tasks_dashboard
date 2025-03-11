package ru.effective.mobile.tasks_dashboard.dto.auth;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    @Schema(description = "Сообщение об успешной регистрации.", example = "Пользователь успешно зарегистрирован!")
    private String message;

    @Schema(description = "Access токен.", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbmRyZXkzMjdAeWFuZGV4Ln...")
    private String accessToken;

    @Schema(description = "Refresh токен.", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbmRyZXkzMjdAeWFuZGV4L...")
    private String refreshToken;
}
