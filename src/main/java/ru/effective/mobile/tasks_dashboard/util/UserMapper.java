package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.effective.mobile.tasks_dashboard.dto.UserInputDto;
import ru.effective.mobile.tasks_dashboard.dto.UserOutputDto;

import ru.effective.mobile.tasks_dashboard.model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

     @Mapping(source = "userInputDto.email", target = "email")
     User userInputDtoToUser(UserInputDto userInputDto);

     @Mapping(source = "user.email", target = "email")
     UserOutputDto userToUserOutputDto(User user);

}
