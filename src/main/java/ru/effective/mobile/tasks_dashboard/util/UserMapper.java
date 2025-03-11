package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.effective.mobile.tasks_dashboard.dto.UserInputDto;
import ru.effective.mobile.tasks_dashboard.dto.UserOutputDto;

import ru.effective.mobile.tasks_dashboard.model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

     @Mapping(source = "userInputDto.email", target = "email")
     User userInputDtoToUser(UserInputDto userInputDto);

     @Mapping(source = "user.id", target = "id")
     @Mapping(source = "user.email", target = "email")
     @Mapping(source = "user.roles", target = "roles")
     UserOutputDto userToUserOutputDto(User user);

}
