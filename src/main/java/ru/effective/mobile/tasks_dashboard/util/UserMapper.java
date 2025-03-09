package ru.effective.mobile.tasks_dashboard.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.effective.mobile.tasks_dashboard.dto.UserInputDto;
import ru.effective.mobile.tasks_dashboard.dto.UserOutputDto;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.User;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {


     // Преобразование UserInputDto → User
     @Mapping(target = "id", ignore = true) // id генерируется автоматически
     @Mapping(target = "createdAt", ignore = true) // createdAt заполняется автоматически
     @Mapping(target = "updatedAt", ignore = true) // updatedAt заполняется автоматически
     @Mapping(target = "roles", ignore = true) // роли устанавливаются отдельно
     User userInputDtoToUser(UserInputDto dto);

     // Преобразование User → UserOutputDto
     @Mapping(source = "id", target = "id")
     @Mapping(source = "createdAt", target = "createdAt")
     @Mapping(source = "updatedAt", target = "updatedAt")
     @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoles")
     UserOutputDto userToUserOutputDto(User user);

}
