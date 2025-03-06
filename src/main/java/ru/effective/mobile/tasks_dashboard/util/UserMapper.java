package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import ru.effective.mobile.tasks_dashboard.dto.UserDto;
import ru.effective.mobile.tasks_dashboard.model.User;

@Mapper
public interface UserMapper {
     User dtoToEntity(UserDto dto);
     UserDto entityToDto(User user);
}
