package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import ru.effective.mobile.tasks_dashboard.dto.UserDto;
import ru.effective.mobile.tasks_dashboard.model.User;

@Mapper
public interface UserMapper {
     User userDtoToUser(UserDto dto);
     UserDto userToUserDto(User user);
}
