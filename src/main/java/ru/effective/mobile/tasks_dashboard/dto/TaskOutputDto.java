package ru.effective.mobile.tasks_dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.effective.mobile.tasks_dashboard.dto.base.TaskBaseDto;
import ru.effective.mobile.tasks_dashboard.dto.base.UserBaseDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskOutputDto extends TaskBaseDto {

    private long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private UserOutputDto author;
    private UserOutputDto executor;
}