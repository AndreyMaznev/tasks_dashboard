package ru.effective.mobile.tasks_dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.effective.mobile.tasks_dashboard.dto.base.TaskBaseDto;
import ru.effective.mobile.tasks_dashboard.dto.base.UserBaseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskInputDto extends TaskBaseDto {

    private Long author;

    private Long executor;
}
