package ru.effective.mobile.tasks_dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UserInputDto {

    @Schema(description = "Email пользователя.", example = "user@example.com")
    private String email;
}